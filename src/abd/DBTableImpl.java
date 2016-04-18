package abd;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Utils;
import abd.phys.DefaultPageSequentialAccess;
import abd.schemas.DBTable;
import abd.schemas.TableDescription;

public class DBTableImpl implements DBTable{

	public int NB_MAX_RECORDS;
	public static int PAGE_SIZE = SGBD.PAGE_SIZE;

	Path dataFolderPath;
	DefaultPageSequentialAccess page; // Current page loaded
	String currentPageFilename; // Current filename for the page loaded
	List<String> pagesFilename; // List of the pages filename
	TableDescription defTabDesc; // Table desc

	int nextFilename = 0x00; // Name for the next page created

	// First key is the column rank for the index
	// The column rank lead to a TreeMap which represents all the indexes for THIS columnRank
	// String [] is always length 2. [0] is pageFilename. [1] is pos into the page (the page of name [0])
	public Map<Integer, Map<String, List<String[]>>> indexMap;
	
	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc){
			
		this.defTabDesc = defTabDesc;
		page = null;
		this.dataFolderPath = dataFolderPath;
		pagesFilename = getListPageFiles();
		NB_MAX_RECORDS = PAGE_SIZE / getNbBytesPerRecord();
		indexMap = new HashMap<Integer, Map<String, List<String[]>>>();


	}

	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc, int indexColumnRank){
		this(dataFolderPath, defTabDesc);
		indexMap.put(indexColumnRank, new HashMap<String, List<String[]>>());
		this.indexMap = loadIndexes();
	}

	public DBTableImpl(Path dataFolderPath, TableDescription defTabDesc, int [] indexesColumnRank){
		this(dataFolderPath, defTabDesc);
		for(Integer index : indexesColumnRank){
			indexMap.put(index, new HashMap<String, List<String[]>>());
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
			Map<String, List<String[]>> map = indexMap.get(columnRank);
			String key = new String(Utils.getColumnFromColumnRank(defTabDesc, columnRank, tupleValue));

			List<String []> posList = map.get(key);
			if(posList == null)
				posList = new ArrayList<String []>();
			posList.add(posTab);
			map.put(key, posList);

		}



		saveCurrentPage();

		saveAllIndexes();

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
			Map<String, List<String[]>> map = indexMap.get(index);
			System.out.println("Index : " + map.keySet().toString());

			String key = new String(Utils.getColumnFromColumnRank(defTabDesc, index, tupleValue));
			List<String []> posList = sortPosList(map.get(key));
			
			for(String []posElement : posList){
				loadPage(posElement[0]);
				
				int pos = 0;
				byte[] record = null;
				while(pos != Integer.parseInt(posElement[1])){		
					record = page.getNextRecord();
					pos += record.length;
				}
				
				if(Arrays.equals(record, tupleValue))
					return currentPageFilename;
			}
			// Si return null, il y a un problème
			return null;


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
		
		Map<String, List<String[]>> map = indexMap.get(columnRank);
		if(map != null && map.get(attributeValue) != null){
			
			
			loadPage(map.get(attributeValue).get(0)[0]);

			page.resetPosition();

			int cpt = 0;
			int posIntoPage = Integer.parseInt(map.get(attributeValue).get(0)[1]);
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
		String pageFilename = getPageOfTuple(TupleValue);

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
				Map<String, List<String[]>> map = indexMap.get(columnRank);
				String column = new String(Utils.getColumnFromColumnRank(defTabDesc, columnRank, TupleValue));
				map.remove(column);
			}			
		}
		saveCurrentPage();
	}


	private void loadPage(String filename){
		if(currentPageFilename != filename){
			try {
				currentPageFilename = filename;
				FileInputStream in = new FileInputStream(dataFolderPath + "/" + currentPageFilename);
				byte [] b = new byte[PAGE_SIZE];
				in.read(b);

				page = new DefaultPageSequentialAccess(ByteBuffer.wrap(b), getNbBytesPerRecord());

				in.close();
			} catch(Exception e){
				e.printStackTrace();
			}	
		}
	}

	private Map<Integer, Map<String, List<String[]>>> loadIndexes(){
		Map<Integer, Map<String, List<String[]>>> index = new HashMap<Integer, Map<String, List<String[]>>>();


		ArrayList<String> indexes = getListIndexFiles();
		for(String filename : indexes){
			try {
				FileInputStream fin = new FileInputStream(dataFolderPath + "/" + filename);
				ObjectInputStream ois = new ObjectInputStream(fin);   
				Map<String, List<String[]>> map = (Map<String, List<String[]>>)ois.readObject();
				int indColumn = Integer.parseInt(filename.split("\\.")[0]);

				index.put(indColumn, map);

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

	private List<String []> sortPosList(List<String []> posList){
		String [][]array= new String[posList.size()][2];
		int i=0;
		for(String[] posElement : posList){
			array[i] = posElement;
			i++;
		}
		
		Arrays.sort(array, new DefaultComparator());
		return Arrays.asList(array);
	}
	
	@Override
	public void close() throws IOException {

	}

	@Override
	public void open() throws IOException {

	}

	@Override
	public byte[] nextRecord() throws IOException {
		// Si pas de page courante chargee, on est au debut, et on charge donc la premiere page
		if(page == null){
			loadPage(pagesFilename.get(0));
		}
		byte [] nextRecord = page.getNextRecord();
		// Si on est a la fin de la page
		if(nextRecord != null)
			return nextRecord;
		
		int i = pagesFilename.indexOf(currentPageFilename);
		
		// Si on est a la fin de la derniere page et qu'il n'y a plus de page a charger derriere
		if((i+1) >= pagesFilename.size())
			return null;
		
		loadPage(pagesFilename.get(i+1));
		return page.getNextRecord();
		
	}
	
	private class DefaultComparator implements Comparator<String[]> {

		@Override
		public int compare(String[] o1, String[] o2) {
			return o1[0].compareTo(o2[0]);
		}
	
	}

}
