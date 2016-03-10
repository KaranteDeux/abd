package abd.reldb.ra.phys;

import java.io.IOException;

/** Performs a join operation on two tables, using the nested loops algorithm.
 * A maximal number of pages that can be used is provided at construction time.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 10 mars 2016
 */
public class DefaultNestedLoopsJoin implements PhysicalOperator {

	public DefaultNestedLoopsJoin (String tableName1, String tableName2, int maxNumberPages) {
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	/** Computes the join, storing the result in the result table. */
	public void join() {
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
