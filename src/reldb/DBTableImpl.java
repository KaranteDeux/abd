package reldb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tp2.PageRandomAccessImpl;
import abd.reldb.DBTable;
import abd.reldb.TableDescription;
import abd.tp2.Page;

public class DBTableImpl implements DBTable{
	
	public static int NB_MAX_RECORDS = 256;

	List<Page> pages;
	TableDescription defTabDesc;
	
	// int columnRank
	// 
	
	
	public DBTableImpl(TableDescription defTabDesc){
		this.defTabDesc = defTabDesc;
		pages = new ArrayList<Page>();
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
		
		// sauvegarder dans disque
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
		return posTuple(tupleValue) != -1;
	}

	public int posTuple(byte[] tupleValue) throws IOException {

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
	
	@Override
	public byte[] getTupleBySelection(int columnRank, byte[] attributeValue) throws IOException {
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

	@Override
	public void deleteTuple(byte[] TupleValue) throws IOException {
		int numPage = posTuple(TupleValue);
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
