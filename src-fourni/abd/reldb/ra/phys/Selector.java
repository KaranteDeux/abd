package abd.reldb.ra.phys;

/** Allows to test whether a tuple satisfies some selection criterion. 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 25 févr. 2016
 */
public interface Selector {

	public boolean isSelected(byte[] record); 
	
}
