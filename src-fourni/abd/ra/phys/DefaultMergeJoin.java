package abd.ra.phys;

import java.io.IOException;

/** Performs a merge join of two tables.
 * The tables are supposed to be sorted according to the join criterion.
 * The merge sort is a {@link PhysicalOperator} in the sense that we can iterate on the result.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 10 mars 2016
 */
public class DefaultMergeJoin implements PhysicalOperator {

	public DefaultMergeJoin (String tableName1, String tableName2) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void close() throws IOException {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public byte[] nextRecord() throws IOException {
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	
}
