package abd.reldb.ra;

import java.util.Arrays;
import java.util.Comparator;

import abd.reldb.AttributeType;

/** A criterion for selection that defines the comparison of the value of an attribute with a constant.
 * The comparison is performed using an ordering relation given as a {@link Comparator}.
 * The comparator is always to be applied with the attribute value as first argument, and the constant as second argument.
 * The {@link #direction} argument of the constructor defines whether the selection requires for the attribute value to precede, or to
 * succeed the constant. Denoting {@code attrValue} the value of the attribute having the columnRank on which the comparison applies, and
 * denoting {@code constant} the constant to which we want to compare: 
 * 
 *  a selection {@code attrValue < constant} should have {@code direction < 0} and {@code isStrict = true} 
 *  a selection {@code attrValue <= constant} should have {@code direction < 0} and {@code isStrict = false}
 *  a selection {@code attrValue > constant} should have {@code direction > 0} and {@code isStrict = true}
 *  a selection {@code attrValue >= constant} should have {@code direction > 0} and {@code isStrict = false}
 *  
 * For a selection operation that tests the equality of the value of an attribute with a constant, one has to use {@link SelectionCriterion_EqualityToConstant}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class SelectionCriterion_ComparisonWithConstant implements SelectionCriterion {

	private final int columnRank;
	private final byte[] constant;
	private final int direction;
	private final boolean isStrict;
	private final Comparator<byte[]> comparator;
	
	public SelectionCriterion_ComparisonWithConstant(int columnRank, byte[] constant, Comparator<byte[]> comp, int direction, boolean isStrict) {
		if (direction == 0)
			throw new IllegalArgumentException("Comparison should have a non zero direction.");
		this.constant = Arrays.copyOf(constant, constant.length);
		this.columnRank = columnRank;
		this.direction = direction;
		this.isStrict = isStrict;
		this.comparator = comp;
	}
	
	public int getColumnRank() {
		return columnRank;
	}

	public byte[] getConstant() {
		return Arrays.copyOf(constant, constant.length);
	}

	public int getDirection() {
		return direction;
	}

	public boolean isStrict() {
		return isStrict;
	}

	public Comparator<byte[]> getComparator() {
		return comparator;
	}

	@Override
	public String toString() {
		String operator;
		if (direction < 0) {
			operator = (isStrict ? "<" : "<=");
		} else {
			operator = (isStrict ? ">" : ">=");
		}
		return String.format("%d%s%s", columnRank, operator, AttributeType.toString(constant));
	}
	
}
