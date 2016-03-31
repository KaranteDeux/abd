package abd.ra;

import java.util.Arrays;

import abd.schemas.AttributeType;

/** A criterion for selection that tests the equality of the value of an attribute with a constant.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class SelectionCriterion_EqualityToConstant implements SelectionCriterion {

	private int columnRank;
	private byte[] constant;
	
	public SelectionCriterion_EqualityToConstant(int columnRank, byte[] constant) {
		this.constant = Arrays.copyOf(constant, constant.length);
		this.columnRank = columnRank;
	}	

	@Override
	public String toString() {
		return String.format("%d=%s", columnRank,  AttributeType.toString(constant));
	}

	public byte[] getConstant() {
		return Arrays.copyOf(constant, constant.length);
	}

	public int getColumnRank() {
		return columnRank;
	}


	
}
