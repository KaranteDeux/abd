package reldb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import tp2.PageRandomAccessImpl;
import abd.reldb.DBTable;
import abd.reldb.TableDescription;
import abd.tp2.Page;

public class DBTableImpl implements DBTable{

	public static int NB_MAX_RECORDS = 256;

	List<Page> pages;
	TableDescription defTabDesc;

	// First key is the column rank for the index
	// The column rank lead to a TreeMap which represents all the indexes for THIS columnRank
	// int [] is always length 2. [0] is page nb. [1] is pos into the page
	Map<Integer, Map<String, int[]>> indexMap;


	public DBTableImpl(TableDescription defTabDesc){
		this.defTabDesc = defTabDesc;
		pages = new ArrayList<Page>();
		indexMap = new TreeMap<Integer, Map<String, int[]>>();
	}

	public DBTableImpl(TableDescription defTabDesc, int indexColumnRank){
		this(defTabDesc);
		indexMap.put(indexColumnRank, new TreeMap<String, int[]>());
	}

	public DBTableImpl(TableDescription defTabDesc, int [] indexesColumnRank){
		this(defTabDesc);
		for(Integer index : indexesColumnRank){
			indexMap.put(index, new TreeMap<String, int[]>());
		}
	}


	@Override
	public TableDescription getTableDescription() {
		return defTabDesc;
	}

	@Override
	public void addTuple(byte[] tupleValue) throws IOException {
		// 256*256 + 4*256 (tableau de metadata) + 4 (un entier pour nb de tuples dans la page)
		int posPage = getFirstPageNotFull();

		Page page = null;
		if(posPage >= pages.size()){
			ByteBuffer byteBuffer = ByteBuffer.allocate(256*260+4);
			page = new PageRandomAccessImpl(byteBuffer, NB_MAX_RECORDS, 256);			
			pages.add(page);

		} else {
			page = pages.get(posPage);
		}
		page.addRecord(tupleValue);

		// Ajout dans les index
		List<Integer> keys = new ArrayList<Integer>(indexMap.keySet());

		int[] posTab = new int []{posPage, getPosIntoPageOfTuple(posPage, tupleValue)};

		for(Integer columnRank : keys){

			Map<String, int[]> map = indexMap.get(columnRank);

			map.put(new String(getColumnFromColumnRank(columnRank, tupleValue)), posTab);

		}

		// sauvegarder dans disque
	}

	private byte[] getColumnFromColumnRank(int columnRank, byte[] tuple){

		int posDansTuple = 0, sizeColumn = defTabDesc.getAttributeType(columnRank).getLength();
		for(int pos = 0;pos<columnRank;pos++){
			posDansTuple += defTabDesc.getAttributeType(pos).getLength();
		}
		return Arrays.copyOfRange(tuple, posDansTuple, posDansTuple + sizeColumn);

	}

	private int getFirstPageNotFull(){
		int i=0;
		boolean found = false;
		while(i < pages.size() && !found){
			if(pages.get(i).getNbRecords() != NB_MAX_RECORDS)
				found = true;
			else
				i++;
		}
		return i;
	}

	@Override
	public boolean existsTuple(byte[] tupleValue) throws IOException {
		return getPageOfTuple(tupleValue) != -1;
	}

	public int getPageOfTuple(byte[] tupleValue) throws IOException {

		if(!indexMap.keySet().isEmpty()){
			// We take first column having an index
			int index = new ArrayList<Integer>(indexMap.keySet()).get(0);
			Map<String, int[]> map = indexMap.get(index);

			// S'il y a un index
			byte[] column = getColumnFromColumnRank(index, tupleValue);
			return map.get(column)[0];



		} else {
			// S'il n'y a pas d'index



			int numPage = -1;
			int i = 0;
			boolean found = false;
			while(i < pages.size() && !found){
				Page page = pages.get(i);
				page.resetPosition();

				byte[] record = page.getNextRecord(); 
				while(record != null && !record.equals(tupleValue)){		
					record = page.getNextRecord();
				}

				if(record != null){
					numPage = i;
					found = true;
				}

				i++;
			}

			return numPage;
		}
	}


	public int getPosIntoPageOfTuple(int pageNb, byte[] tupleValue) throws IOException {

		Page page = pages.get(pageNb);
		page.resetPosition();
		int pos = 0;

		byte[] record = page.getNextRecord(); 
		while(record != null && !record.equals(tupleValue)){		
			record = page.getNextRecord();
			pos += record.length;
		}


		return pos;
	}

	@Override
	public byte[] getTupleBySelection(int columnRank, byte[] attributeValue) throws IOException {
		Map<String, int[]> map = indexMap.get(columnRank);
		if(map != null && map.get(attributeValue) != null){
			Page page = pages.get(map.get(attributeValue)[0]);
			page.resetPosition();

			int cpt = 0;
			int posIntoPage = map.get(attributeValue)[1];
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

			int i = 0;
			boolean found = false;
			while(i < pages.size() && !found){
				Page page = pages.get(i);
				page.resetPosition();

				record = page.getNextRecord(); 
				while(record != null && !Arrays.copyOfRange(record, posDansTuple, posDansTuple + sizeColumn).equals(attributeValue)){		
					record = page.getNextRecord();
				}

				if(record != null){
					found = true;
				}

				i++;
			}

			return record;
		}
	}

	@Override
	public void deleteTuple(byte[] TupleValue) throws IOException {
		int numPage = getPageOfTuple(TupleValue);
		if(numPage != -1){
			Page page = pages.get(numPage);
			page.remove();
		}
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public void open() throws IOException {

	}



}
