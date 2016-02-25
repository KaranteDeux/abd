package abd.reldb.ra.phys;

/** A generalization of a combination of selection operations nested within projection operations. 
 * For a given record, the {@link #filter(byte[])} method returns {@code null} if that record is not 
 * accepted by the filter (i.e. does not satisfy the selection criteria), or otherwise returns a new 
 * record that results in projecting out the record given as parameter according to the projection
 * criterions.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public interface RecordFilter {

	/** Returns the result of the filtering, or null if the filter does not accept the record. 
	 * 
	 * @param record A record to be filtered 
	 * @return The resulting record
	 */
	public byte[] filter(byte[] record);	

}
