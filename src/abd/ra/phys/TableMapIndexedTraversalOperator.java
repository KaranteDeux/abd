package abd.ra.phys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import abd.Factory;
import abd.SGBD;
import abd.phys.DefaultPageSequentialAccess;
import abd.phys.Page;
import abd.schemas.DBTable;
import abd.schemas.TableDescription;

public class TableMapIndexedTraversalOperator implements PhysicalOperator {

	DBTable table;
	byte [] attributeValue;
	int columnRank;

	Path dataFolder;
	String tableName;

	// String [] is always length 2. [0] is pageFilename. [1] is pos into the page (the page of name [0])
	List<String[]> listPositions;
	Iterator<String[]> iterator;

	public TableMapIndexedTraversalOperator(String tableName, TableDescription tableDescr, byte[] attributeValue, int indexedColumnRank) throws IOException {
		this.dataFolder = SGBD.getTableFolder(tableName);

		table = Factory.newDBTableWithIndex(dataFolder, tableDescr, indexedColumnRank);

		this.tableName = tableName;
		this.attributeValue = attributeValue; 
		this.columnRank = indexedColumnRank;


		Map<String, List<String[]>> index =  loadIndexes(dataFolder);

		listPositions = index.get(attributeValue);

		iterator = listPositions.iterator();

	}

	@Override
	public byte[] nextRecord() throws IOException {
		while(iterator.hasNext()){
			String[] pos = iterator.next();


			Page page = loadPage(pos[0]);

			page.resetPosition();

			int cpt = 0;
			int posIntoPage = Integer.parseInt(pos[1]);
			byte[] record = page.getNextRecord(); 
			while(cpt != posIntoPage){		
				record = page.getNextRecord();
				cpt += record.length;
			}

			return record;

		}

		return null;

	}

	@Override
	public void close() throws IOException {

	}

	private Page loadPage(String filename){
		Page page = null;
		try {
			FileInputStream in = new FileInputStream(dataFolder + "/" + filename);
			byte [] b = new byte[SGBD.PAGE_SIZE];
			in.read(b);

			page = new DefaultPageSequentialAccess(ByteBuffer.wrap(b), SGBD.getRelation(tableName).getTableDescription().getTupleLength());

			in.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return page;

	}



	private Map<String, List<String[]>> loadIndexes(Path dataFolder){
		Map<String, List<String[]>> index = null;
		
		try {
			index = new HashMap<String, List<String[]>>();

			/* SGBD.getIndexFileName(tableName, indexedColumnRank); */
			ArrayList<String> indexes = getListIndexFiles(dataFolder);
			Path index_file = SGBD.getIndexFileName(tableName, columnRank);

			FileInputStream fin = new FileInputStream(index_file.toString());
			ObjectInputStream ois = new ObjectInputStream(fin);   
			Map<String, List<String[]>> map = (Map<String, List<String[]>>)ois.readObject();


			ois.close();
		} catch(Exception e){
			e.printStackTrace();
		}



		return index;


	}


	private ArrayList<String> getListIndexFiles(Path dataFolder){
		File repertoire = new File(dataFolder.toString());
		ArrayList<String> indexes = new ArrayList<String>();
		for(String filename : repertoire.list()){
			if(filename.contains("INDEX"))
				indexes.add(filename);
		}
		return indexes;
	}
}