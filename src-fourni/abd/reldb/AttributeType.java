package abd.reldb;

import static abd.reldb.Datatype.*;

/** Describes the type of an attribute of a relation.
 * An attribute type is characterized by its length and its datatype, except for a decimal that has also a scale.  
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 janv. 2016
 */
public class AttributeType {

	/** The datatype of this attribute type. */
	public final Datatype datatype;
	/** The length (in bytes) of the datatype. 
	 * Used as length for character types (CHARACTER, VARCHAR) or precision for numerical types (DECIMAL, INTEGER). */
	private final int length;
	/** The scale of a DECIMAL */
	private final int scale;
	
	private AttributeType (Datatype datatype, int length, int scale) {
		this.datatype = datatype;
		this.length = length;
		this.scale = scale;
		
	}
	
	public static AttributeType newCharacter (int length) {
		return new AttributeType (CHARACTER, length, -1);
	}
	
	public static AttributeType newVarchar (int maxLength) {
		return new AttributeType(VARCHAR, maxLength, -1);
	}
	
	public static AttributeType newBoolean () {
		return new AttributeType(BOOLEAN, 1, -1);
	}
	
	public static AttributeType newInteger (int precision) {
		return new AttributeType(INTEGER, precision, -1);
	}
	
	public static AttributeType newDecimal (int precision, int scale) {
		return new AttributeType(DECIMAL, precision, scale);
	}
	
	public static AttributeType newTimestamp () {
		return new AttributeType(TIMESTAMP, 8, -1); // Stored as a long
	}
	
	/**  The length in bytes.
	 * Defined for all datatypes.
	 * @return the length in bytes
	 */
	public int getLength() {
		return length;
	}
	
	/** The precision (number of total digits) of a DECIMAL.
	 * Undefined for other data types.
	 * @return the precision of a DECIMAL
	 * @throws UnsupportedOperationException for other datatypes
	 */
	public int getPrecision () {
		if (datatype == DECIMAL) return length;
		else throw new UnsupportedOperationException("Precision is undefined for " + datatype);
		
	}
	
	/** The scale (number of digits after the decimal point) of a DECIMAL.
	 * Undefined for other data types.
	 * @return the scale of a decimal
	 * @throws UnsupportedOperationException for other datatypes
	 */
	public int getScale () {
		if (datatype == DECIMAL) return scale;
		else throw new UnsupportedOperationException("Scale is undefined for " + datatype);
	}
}
