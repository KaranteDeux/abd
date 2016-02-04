package tp2;

import java.nio.ByteBuffer;

import abd.tp2.Page;

public class DefaultPageSequentialAccess implements Page {

	public static final int PAGE_SIZE_BYTES = 4096;
	private ByteBuffer buff;
	private int nbRecords;                                          
	private int recordSize;
	
	
	public DefaultPageSequentialAccess(ByteBuffer buff,int size){
		this.buff= buff;
		initPage();
		this.recordSize = size;
	}
	
	public void initPage(){
		this.nbRecords = 0;
		this.buff.clear();
	}
	
	public void setNbRecords(int nbRecords) {
		this.nbRecords = nbRecords;
	}

	public int getNbRecords(int nbRecords) {
		return this.nbRecords;
	}

	public int getRecordSize() {
		return recordSize;
	}



	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}



	public ByteBuffer getBuff() {
		return buff;
	}


	public void setBuff(ByteBuffer buff) {
		this.buff = buff;
	}



	@Override
	public long getNbRecords() {
		return nbRecords;
	}

	@Override
	public byte[] getNextRecord() {
		if(!buff.hasRemaining())
			return null;
		
		byte[] array = new byte[recordSize];
		this.buff.mark();
		buff.get(array);
		return array;
	}

	@Override
	public void remove() {
		if(nbRecords <= 0)
			return;
		this.nbRecords--;
		byte [] b = new byte[recordSize]; 
		for(int i = 0; i < b.length; i++) {
	        b[i] = '\0';
	    }
		buff.reset();

		setRecord(b);
	}

	@Override
	public void resetPosition() {
		this.buff.clear();

	}

	@Override
	public void setRecord(byte[] newRecord) {
		this.buff.put(newRecord);
	}

	@Override
	public boolean addRecord(byte[] newRecord) {

		this.setRecord(newRecord);
		this.nbRecords++;

		return true;
	}

}
