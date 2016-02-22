package reldb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tp2.DefaultPageSequentialAccess;
import abd.reldb.DBTable;
import abd.reldb.TableDescription;

public class DBTableImpl implements DBTable{

	public int NB_MAX_RECORDS;
	public int PAGE_SIZE = 3 * 512;

	Path dataFolderPath;
	DefaultPageSequentialAccess page; // Current page loaded
	String currentPageFilename; // Current filename for the page loaded
	List<String> pagesFilename; // List of the pages filename
	TableDescription defTabDesc; // Table desc

	int nextFilename = 0x00; // Name for the next page created

	// First key is the column rank for the index
	// The column rank lead to a TreeMap which represents all the indexes for THIS columnRank
	// String [] is always length 2. [0] is pageFilename. [1] is pos into the page (the page of name [0])
	public Map<Integer, Map<String, String[]>> indexMap;

	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc){
		this.defTabDesc = defTabDesc;
		page = null;
		this.dataFolderPath = dataFolderPath;
		pagesFilename = getListPageFiles();
		NB_MAX_RECORDS = PAGE_SIZE / getNbBytesPerRecord();
		indexMap = new HashMap<Integer, Map<String, String[]>>();
		
		
	}

	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc, int indexColumnRank){
		this(dataFolderPath, defTabDesc);
		indexMap.put(indexColumnRank, new HashMap<String, String[]>());
		this.indexMap = loadIndexes();
	}

	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc, int [] indexesColumnRank){
		this(dataFolderPath, defTabDesc);
		for(Integer index : indexesColumnRank){
			indexMap.put(index, new HashMap<String, String[]>());
		}
		this.indexMap = loadIndexes();
	}



	@Override
	public TableDescription getTableDescription() {
		return defTabDesc;
	}

	@Override
	public void addTuple(byte[] tupleValue) throws IOException {
		// 256*256 + 4*256 (tableau de metadata) + 4 (un entier pour nb de tuples dans la page)
		String fileName = getFirstPageNotFull();


		if(fileName == null){

			ByteBuffer byteBuffer = ByteBuffer.allocate(PAGE_SIZE);
			page = new DefaultPageSequentialAccess(byteBuffer, getNbBytesPerRecord());			
			String newFilename = Integer.toHexString(nextFilename);
			pagesFilename.add(newFilename);
			currentPageFilename = newFilename;
			nextFilename += 1;
		} else {
			loadPage(fileName);
			currentPageFilename = fileName;
		}
		page.addRecord(tupleValue);

		// Ajout dans les index
		List<Integer> keys = new ArrayList<Integer>(indexMap.keySet());

		String[] posTab = new String []{currentPageFilename, getPosIntoPageOfTuple(currentPageFilename, tupleValue) + ""}; // Convert into String

		for(Integer columnRank : keys){
			Map<String, String[]> map = indexMap.get(columnRank);
			String key = new String(getColumnFromColumnRank(columnRank, tupleValue));
			if(map.get(key) == null){ // S'il n'y a pas déjà d'entrées pour cette clef
				map.put(key, posTab);
			}


		}



		saveCurrentPage();

		saveAllIndexes();

	}

	private byte[] getColumnFromColumnRank(int columnRank, byte[] tuple){

		int posDansTuple = 0, sizeColumn = defTabDesc.getAttributeType(columnRank).getLength();
		for(int pos = 0;pos<columnRank;pos++){
			posDansTuple += defTabDesc.getAttributeType(pos).getLength();
		}
		return Arrays.copyOfRange(tuple, posDansTuple, posDansTuple + sizeColumn);

	}

	private String getFirstPageNotFull(){
		for(String pageFilename : pagesFilename){
			loadPage(pageFilename);

			if(page.getNbRecords() != NB_MAX_RECORDS){
				return pageFilename;
			}
		}
		return null;
	}

	@Override
	public boolean existsTuple(byte[] tupleValue) throws IOException {
		return getPageOfTuple(tupleValue) != null;
	}

	public String getPageOfTuple(byte[] tupleValue) throws IOException {


		if(!indexMap.keySet().isEmpty()){
			// S'il y a un index
			// We take first column having an index
			int index = new ArrayList<Integer>(indexMap.keySet()).get(0);
			Map<String, String[]> map = indexMap.get(index);
			System.out.println("Index : " + map.keySet().toString());

			String key = new String(getColumnFromColumnRank(index, tupleValue));
			System.out.println("Column : " + key);

			System.out.println("ind[0] : " + map.get(key));
			System.out.println((map.keySet().toString()));
			return map.get(key)[0];

		} else {
			// S'il n'y a pas d'index

			for(String pageFilename : pagesFilename){
				loadPage(pageFilename);
				page.resetPosition();

				byte[] record = page.getNextRecord(); 

				//System.out.println("Bytes : " + Arrays.toString(page.getBuff().array()));
				System.out.println("Start");
				while(record != null && !Arrays.equals(record, tupleValue)){		
					record = page.getNextRecord();

				}
				System.out.println("End");
				if(record != null){
					page.resetPosition();
					return pageFilename;

				}
			}
			page.resetPosition();

			return null;
		}
	}

	public int getPosIntoPageOfTuple(String pageFilename, byte[] tupleValue) throws IOException {

		loadPage(pageFilename);
		page.resetPosition();
		int pos = 0;

		byte[] record = page.getNextRecord(); 
		while(record != null && !Arrays.equals(record, tupleValue)){
			pos += record.length;
			record = page.getNextRecord();

		}

		page.resetPosition();

		return pos;
	}

	@Override
	public byte[] getTupleBySelection(int columnRank, byte[] attributeValue) throws IOException {
		Map<String, String[]> map = indexMap.get(columnRank);
		if(map != null && map.get(attributeValue) != null){
			loadPage(map.get(attributeValue)[0]);

			page.resetPosition();

			int cpt = 0;
			int posIntoPage = Integer.parseInt(map.get(attributeValue)[1]);
			byte[] record = page.getNextRecord(); 
			while(cpt != posIntoPage){		
				record = page.getNextRecord();
				cpt += record.length;
			}

			return record;
		} else {
			int posDansTuple = 0, sizeColumn = attributeValue.length;
			for(int pos = 0;pos<columnRank;pos++){
				posDansTuple += defTabDesc.getAttributeType(pos).getLength();
			}

			byte[] record = null;

			for(String pageFilename : pagesFilename){
				loadPage(pageFilename);
				page.resetPosition();

				record = page.getNextRecord(); 
				while(record != null && !Arrays.equals(Arrays.copyOfRange(record, posDansTuple, posDansTuple + sizeColumn), attributeValue)){		
					record = page.getNextRecord();

				}

				if(record != null){
					return record;
				}

			}

			return record;
		}
	}

	// deleteTuple n'utilise le fait qu'il y a potentiellement des index
	// Ça serait une chose à faire, plus tard, si on a le temps

	@Override
	public void deleteTuple(byte[] TupleValue) throws IOException {
		System.out.println("START DELETE");
		System.out.println("Tuple " + Arrays.toString(TupleValue));
		
		String pageFilename = getPageOfTuple(TupleValue);

		System.out.println("Page filename : " + pageFilename);
		if(pageFilename != null){
			loadPage(pageFilename);

			byte [] record = null;
			while(!Arrays.equals(TupleValue, record)){
				record = page.getNextRecord();
			}

			page.remove();
			//System.out.println(Arrays.toString(page.getBuff().array()));

			List<Integer> keys = new ArrayList<Integer>(indexMap.keySet());

			for(Integer columnRank : keys){
				Map<String, String[]> map = indexMap.get(columnRank);
				String column = new String(getColumnFromColumnRank(columnRank, TupleValue));
				map.remove(column);
			}			
		}
		saveCurrentPage();
		System.out.println("END DELETE");

	}


	private void loadPage(String filename){
		if(currentPageFilename != filename){
			try {
				currentPageFilename = filename;
				FileInputStream in = new FileInputStream(dataFolderPath + "/" + currentPageFilename);
				byte [] b = new byte[PAGE_SIZE];
				in.read(b);
				//in.close();

				page = new DefaultPageSequentialAccess(ByteBuffer.wrap(b), getNbBytesPerRecord());

			} catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	private Map<Integer, Map<String, String[]>> loadIndexes(){
		Map<Integer, Map<String, String[]>> index = new TreeMap<Integer, Map<String, String[]>>();


		ArrayList<String> indexes = getListIndexFiles();
		for(String filename : indexes){
			try {
				FileInputStream fin = new FileInputStream(dataFolderPath + "/" + filename);
				ObjectInputStream ois = new ObjectInputStream(fin);   
				Map<String, String[]> map = (Map<String, String[]>)ois.readObject();
				int indColumn = Integer.parseInt(filename.split("\\.")[0]);
				
				index.put(indColumn, map);
				
				System.out.println("blabla : " + indColumn);
				System.out.println("map : " + index.get(0).keySet().toString());
				ois.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		
		return index;


	}

	private ArrayList<String> getListIndexFiles(){
		File repertoire = new File(dataFolderPath.toString());
		ArrayList<String> indexes = new ArrayList<String>();
		for(String filename : repertoire.list()){
			if(filename.endsWith(".idx"))
				indexes.add(filename);
		}
		return indexes;
	}

	private ArrayList<String> getListPageFiles(){
		File repertoire = new File(dataFolderPath.toString());
		ArrayList<String> pages = new ArrayList<String>();
		for(String filename : repertoire.list()){
			if(!filename.endsWith(".idx"))
				pages.add(filename);
		}
		return pages;
	}

	private void saveCurrentPage(){
		try {
			FileOutputStream out = new FileOutputStream(dataFolderPath + "/" + currentPageFilename);
			out.write(page.getBuff().array());
			out.flush();
			out.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveAllIndexes(){
		for(int key : indexMap.keySet()){

			try {
				FileOutputStream fout = new FileOutputStream(dataFolderPath + "/" + key + ".idx");
				ObjectOutputStream oos = new ObjectOutputStream(fout);   
				oos.writeObject(indexMap.get(key));
				oos.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private int getNbBytesPerRecord(){
		int cpt = 0;
		for(int i=0;i<defTabDesc.getArity();i++){
			cpt += defTabDesc.getAttributeType(i).getLength();
		}
		return cpt;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void open() throws IOException {

	}



}
