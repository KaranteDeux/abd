package abd.reldb;

/** Describes the structure of a database table: the attributes and their types.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 15 janv. 2016
 */
public interface TableDescription {

	/** The arity of the table (its number of attributes). */
	public int getArity();
	
	/** The attribute type for a given rank
	 * 
	 * @param attributeRank
	 * @return
	 */
	public AttributeType getAttributeType (int attributeRank);
	
	public int getColumnOffset(int columnRank);
	
	public String getName();

	int getTupleLength();


	
}
