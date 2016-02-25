package abd.reldb.ra.phys;

import java.io.Closeable;
import java.io.IOException;


/** A physical operator is a possible actual implementation of an operation of the relational algebra.
 * Physical operators are combined for constructing a physical plan and evalating a query.
 * 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public interface PhysicalOperator extends Closeable {
	
	/** The next record of the result, or null if no such record exists.
	 * 
	 * @return
	 * @throws IOException 
	 */
	public byte[] nextRecord() throws IOException;
	
	@Override
	public void close() throws IOException;

}
