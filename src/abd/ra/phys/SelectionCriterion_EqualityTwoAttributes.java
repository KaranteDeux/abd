package abd.ra.phys;

import abd.ra.SelectionCriterion;


/** A selection criterion that tests whether the values of two attributes are equal.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class SelectionCriterion_EqualityTwoAttributes implements SelectionCriterion {

	private int columnRank1;
	private int columnRank2;
	
	public SelectionCriterion_EqualityTwoAttributes(int columnRank1, int columnRank2) {
		this.columnRank1 = columnRank1;
		this.columnRank2 = columnRank2;
	}
	
	@Override
	public String toString() {
		return String.format("%d=%d", columnRank1, columnRank2);
	}

	public int getColumnRank1() {
		return columnRank1;
	}

	public int getColumnRank2() {
		return columnRank2;
	}
	
	

}
