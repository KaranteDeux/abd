package abd.ra.phys;

/** Allows to check a selection criterion. 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 1 mars 2016
 */
public interface Selector {

	/** Tests whether the given tuple satisfies the selection criterion.
	 * 
	 * @param record
	 * @return true if the tuple satisfies the criterion, false otherwise
	 */
	public boolean isSelected(byte[] record); 
	
}
