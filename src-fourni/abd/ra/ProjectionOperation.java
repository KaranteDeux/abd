package abd.ra;

import java.util.ArrayList;
import java.util.Arrays;

/** A projection operation of the relational algebra. 
 * This is a unary operation and is parameterized by a projection criterion, that is a sequence of column ranks on which to project.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class ProjectionOperation implements RAOperation {

	private RAOperation subOp;
	private ArrayList<Integer> columnRanks;
	
	public ProjectionOperation(RAOperation subOperation, int... columnRanks) {
		if (columnRanks.length == 0)
			throw new IllegalArgumentException("projection on 0 columns not allowed");
		subOp = subOperation;
		this.columnRanks = new ArrayList<>();
		for (Integer column : columnRanks){
			this.columnRanks.add(column);
		}
	}
	
	@Override
	public String toString() {
		return String.format("PROJECT%s(%s)", Arrays.toString(columnRanks.toArray()), subOp);
	}
	
}
