package tp2;


import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import abd.tp2.Page;

public class DefaultPageSequentialAccess implements Page {


	public static int PAGE_SIZE_BYTES = 3 * 512;
	private ByteBuffer buff;
	private int nbRecords;                                          
	private int recordSize;
	final private int recordSizeWithMarker;

	private int offset;


	public DefaultPageSequentialAccess(ByteBuffer buff, int size){
		this.buff= buff;
		initPage();

		this.offset = -1;

		this.recordSize = size;
		this.recordSizeWithMarker = this.recordSize + 1;
	}

	public void initPage(){
		this.nbRecords = 0;
		resetPosition();
	}

	@Override
	public byte[] getNextRecord() {
		/*	if(!buff.hasRemaining())
			return null;
		 */
		boolean used = false;

		byte[] tmp = new byte[recordSizeWithMarker];
		byte[] realRecord = new byte[recordSize];

		while(!used){
			this.offset++;
			try{
				buff.position(this.offset * recordSizeWithMarker);
				buff.get(tmp);
			}catch(Exception e){
				return null;
			}

			// tmp[0] est l'octet du marqueur
			if(tmp[0] == 1){
				used = true;
				System.arraycopy(tmp, 1, realRecord, 0, recordSize);
			}
		}		

		return realRecord;
	}

	@Override
	public boolean addRecord(byte[] newRecord) {
		boolean used = true;
		byte[] tmp = new byte[recordSizeWithMarker];

		do{

			this.offset++;

			try {
				this.buff.position(offset*recordSizeWithMarker);
				buff.get(tmp);
			} catch(Exception e){
				return false;
			}

			// tmp[0] est l'octet du marqueur
			if(tmp[0] == 0)
				used = false;
		}while(used);

		byte[] newRecordWithMarker = new byte[newRecord.length+1];
		newRecordWithMarker[0] = 1;
		newRecordWithMarker = copyIntoDestinationFrom(newRecord, 1, newRecordWithMarker);

		try {
			this.buff.position(offset*recordSizeWithMarker);
			this.buff.put(newRecordWithMarker);

		} catch(Exception e){
			System.out.println("Error from here !");
			e.printStackTrace();
			return false;
		}

		this.buff.put(this.buff.limit()-1, (byte)((int)getNbRecords()+1));
		return true;
	}

	@Override
	public void remove() {

		byte[] mark = new byte[recordSizeWithMarker];
		mark[0] = 0;
		this.buff.position(offset*recordSizeWithMarker);
		this.buff.put(mark);

		this.buff.put(this.buff.limit()-1, (byte)((int)getNbRecords()-1));
		this.offset++;
	}

	@Override
	public void resetPosition() {
		this.buff.clear();
		this.offset = -1;
	}

	@Override
	public void setRecord(byte[] newRecord) {
		byte tab[] = new byte[newRecord.length+1];
		tab[0] = 1;
		copyIntoDestinationFrom(newRecord, 1, tab);
		this.buff.position(this.offset * recordSizeWithMarker); // pas sur de ça
		this.buff.put(tab);

		getNextRecord();
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

	public byte[] copyIntoDestinationFrom(byte[] from, int pos, byte[] to){
		if((to.length - pos) < from.length)
			return null;

		for(int i=0;i<from.length;i++)
			to[i+pos] = from[i];

		return to;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public long getNbRecords() {
		byte [] array = buff.array();
		return array[array.length-1];


	}

}
