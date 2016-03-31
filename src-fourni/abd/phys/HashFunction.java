package abd.phys;

import abd.schemas.TableDescription;

/** Provides two facility methods for computing a hash key for an attribute value in a tuple.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface HashFunction {

	/** Computes the hash key for an attribute value supposed to be in the interval given by the bounds. 
	 * 
	 * @param tuple
	 * @param beginIndex the index where the attribute value starts
	 * @param endIndex the index immediately following the attribute value
	 * @return
	 */
	public int apply(byte[] tuple, int beginIndex, int endIndex);
	
	/** Computes the hash key for an attribute given by a column rank, supposing that the tuple satisfies a given table description.
	 * 
	 * @param tuple
	 * @param columnRank
	 * @param tableDescr
	 * @return
	 */
	public int apply(byte[] tuple, int columnRank, TableDescription tableDescr);
	
	
	
}
