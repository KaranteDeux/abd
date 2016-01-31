package abd.tp2;

public interface Page {

	/** The number of records on the page.
	 * 
	 * @return
	 */
	public long getNbRecords ();

	/** Retrieves the record at the current position and moves the current position to the next record.
	 * The position initially points to the first record on the page.
	 * 
	 * @return The record on the reading position, or {@code null} if the last record was already read.
	 */
	public byte[] getNextRecord ();
	
	/** Removes the record at the current position and moves the current position to the next record.
	 */
	public void remove();
	
	/** Resets the position to point to the first record on the page.
	 */
	public void resetPosition();
	
	/** Modifies the record on the current position and increments the position to point to the next record.
	 * 
	 * @param newRecord
	 */
	public void setRecord (byte[] newRecord);
	
	/** Adds a new record to the page.
	 * Invalidates the current iteration by {@link #getNextRecord()}.
	 * That is, after calling the add method, the behavior of {@link #getNextRecord()} is undefined. 
	 * 
	 * @param newRecord
	 * @return {@code true} if the record was added, {@code false if not}
	 */
	public boolean addRecord (byte[] newRecord);
	
	
	
}
