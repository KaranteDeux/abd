package tp2;

import java.nio.ByteBuffer;

import abd.tp2.Page;

public class DefaultPageSequentialAccess implements Page {

	private ByteBuffer buff;
	private int nbRecords;                                          
	private int offset;
	private int recordSize;
	
	public DefaultPageSequentialAccess(ByteBuffer buff){
		this.buff = buff;
		this.nbRecords = 0;
		this.offset = 0;
		this.recordSize = 256;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
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
		byte[] array = new byte[recordSize];
		buff.get(array,this.offset,this.recordSize);
		this.offset += this.recordSize;
		if(this.offset >= (this.nbRecords * recordSize))
			return null;
		else
			return array;
	}

	@Override
	public void remove() {
		this.nbRecords--;

	}

	@Override
	public void resetPosition() {
		this.offset = 0;

	}

	@Override
	public void setRecord(byte[] newRecord) {
		this.buff.put(newRecord,this.offset,recordSize);
		this.offset += this.recordSize;
	}

	@Override
	public boolean addRecord(byte[] newRecord) {
		this.setRecord(newRecord);
		this.nbRecords++;
		return false;
	}

}
