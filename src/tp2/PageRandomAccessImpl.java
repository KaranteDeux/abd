package tp2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import abd.tp2.PageRandomAccess;

public class PageRandomAccessImpl implements PageRandomAccess {

	// Nb d'enregistrements
	/*
	 * public static int NB_MAX_RECORD = 256;
	 * public static int RECORD_MAX_SIZE = 512;
	 */
	
	
	ByteBuffer byteBuffer;
	private int metadataSize;
	private int nbMaxRecords;
	private int recordSize;
	
	private int currentPos;
	
	public PageRandomAccessImpl(ByteBuffer byteBuffer, int nbMaxRecords, int recordSize){
		this.byteBuffer = byteBuffer;
		this.nbMaxRecords = nbMaxRecords;
		this.recordSize = recordSize;
		metadataSize = nbMaxRecords + 1;
	
		currentPos = nbMaxRecords * recordSize;
	}
	
	@Override
	public long getNbRecords() {
		
		byte[] result = new byte[4];
		byteBuffer.get(result, nbMaxRecords * recordSize + metadataSize - 4, 4);
		
		ByteBuffer wrap = ByteBuffer.wrap(result);
		return wrap.getInt();
		
	}
	
	// A faire

	@Override
	public void remove() {
		// verifier si la donnée existe
		ByteBuffer dbuf = ByteBuffer.allocate(4);
		dbuf.putInt(-1);
		byte[] bytes = dbuf.array();
		byteBuffer.put(bytes, currentPos, 4);
		currentPos += 4;
	}

	@Override
	public void resetPosition() {
		currentPos = nbMaxRecords * recordSize;
	}

	@Override
	public void setRecord(byte[] newRecord) {
		byteBuffer.put(newRecord, currentPos, recordSize);
	}

	
	@Override
	public boolean addRecord(byte[] newRecord) {
		
		if((int)getNbRecords() >= nbMaxRecords)
			return false;
		
		int startMetadataArray = nbMaxRecords * recordSize;
		List<Integer> list = new ArrayList<Integer>();
		
		// Recupérer tous les indices du tableau utilisé
		for(int i=0;i<nbMaxRecords;i++){
			list.add((int)byteBuffer.get(startMetadataArray + nbMaxRecords));
		}
		
		// Avoir le premier indice non utilisé
		int i=0;
		boolean manquant = false;
		while(i < nbMaxRecords && !manquant){
			if(!list.contains(i))
				manquant = true;
			else
				i++;
		}

		currentPos = recordSize * i;
		setRecord(newRecord);
		
		ByteBuffer dbuf = ByteBuffer.allocate(4);
		dbuf.putInt((int)getNbRecords() + 1);
		byte[] bytes = dbuf.array();
		byteBuffer.put(bytes, nbMaxRecords * recordSize + metadataSize - 4, 4);
		
		ByteBuffer dbufbis = ByteBuffer.allocate(4);
		dbufbis.putInt(i);
		byte[] bytesbis = dbuf.array();
		ByteBuffer wrap = ByteBuffer.wrap(Arrays.copyOfRange(newRecord, 0, 4));
		byteBuffer.put(bytesbis, startMetadataArray + wrap.getInt()*4, 4);
		return true;
		
		
	}

	@Override
	public byte[] getRecord(Comparable key) {
		int keyInt = ((Integer) key);
		int pos = byteBuffer.get(nbMaxRecords * recordSize + keyInt);
		byte[] byteArray = new byte[recordSize];
		byteBuffer.get(byteArray, pos * recordSize, recordSize);
		return byteArray;
	}

	@Override
	public boolean removeRecord(Comparable key) {
		// Verifie s'il existe
		int keyInt = ((Integer) key);

		ByteBuffer dbuf = ByteBuffer.allocate(4);
		dbuf.putInt(-1);
		byte[] bytes = dbuf.array();
		byteBuffer.put(bytes, nbMaxRecords * recordSize + keyInt*4, 4);
		return true;
	}

	@Override
	public byte[] getNextRecord() {
		byte[] pos = new byte[4];
		ByteBuffer wrap = byteBuffer.get(pos, currentPos, 4);
		currentPos += 4;
		return getRecord(wrap.getInt());
		
	}

	@Override
	public Comparable getKey(byte[] record) {
		ByteBuffer wrap = ByteBuffer.wrap(Arrays.copyOfRange(record, 0, 4));
		return wrap.getInt();
	}

	

}
