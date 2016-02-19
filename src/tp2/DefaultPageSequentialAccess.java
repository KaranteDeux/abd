package tp2;


import java.nio.ByteBuffer;

import abd.tp2.Page;

public class DefaultPageSequentialAccess implements Page {

	public static final int PAGE_SIZE_BYTES = 4096;
	private ByteBuffer buff;
	private int nbRecords;                                          
	private int recordSize;
	final private int recordSizeWithMarker;

	private int offset;


	public DefaultPageSequentialAccess(ByteBuffer buff,int size){
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

		while(!used){
			this.offset++;
			buff.position(this.offset * recordSizeWithMarker);
			buff.get(tmp);
			
			// tmp[0] est l'octet du marqueur
			if(tmp[0] == 1)
				used = true;
		}		
		return tmp;
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
			System.out.println(offset + ", " + recordSizeWithMarker);
			this.buff.position(offset*recordSizeWithMarker);
			this.buff.put(newRecordWithMarker);
			
		} catch(Exception e){
			System.out.println("Error from here !");
			e.printStackTrace();
			return false;
		}
			
		this.nbRecords++;

		return true;
	}

	@Override
	public void remove() {
		if(nbRecords <= 0)
			return;
		this.nbRecords--;
		
		byte[] mark = new byte[recordSizeWithMarker];
		mark[0] = 0;
		this.buff.put(mark, offset*recordSizeWithMarker, recordSizeWithMarker);
		
		this.offset++;
	}

	@Override
	public void resetPosition() {
		this.buff.clear();
		this.offset = -1;
	}

	@Override
	public void setRecord(byte[] newRecord) {
		this.buff.put(newRecord);
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

	
	
	@Override
	public long getNbRecords() {
		return this.nbRecords;
	}

}
