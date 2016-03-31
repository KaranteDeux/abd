package abd.tp2;

import abd.phys.Page;

public interface PageRandomAccess<K extends Comparable<K>>  extends Page {
	
	/** Retrieves the record with the given key.
	 * 
	 * @param key
	 * @return
	 */
	public byte[] getRecord(K key);

	/** Removes the record with the given key.
	 * Invalidates the current iteration by {@link #getNextRecord()}.
	 * That is, after calling the remove method, the behavior of {@link #getNextRecord()} is undefined. 
	 * 
	 * @param key
	 * @return {@code true} if a record was removed (i.e. the key existed), {@code false} otherwise.
	 */
	public boolean removeRecord (K key);
	
	/** Behaves as in {@linkplain Page#getNextRecord()}, but guarantees that the iteration over the records is performed in the increasing order for the key. 
	 * 
	 */
	@Override
	public byte[] getNextRecord();
	
	
	/** Retrieves the key of a given record.
	 * 
	 * @param record
	 * @return
	 */
	public K getKey(byte[] record);
	

}
