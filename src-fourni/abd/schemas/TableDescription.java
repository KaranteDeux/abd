package abd.schemas;

/** Describes the structure of a database table: the attributes and their types.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 15 janv. 2016
 */
public interface TableDescription {
	
	/** The name of the table it describes. 
	 * 
	 * @return
	 */
	public String getName ();

	/** The arity of the table (its number of attributes). */
	public int getArity();
	
	/** The attribute type for a given rank
	 * 
	 * @param attributeRank
	 * @return
	 */
	public AttributeType getAttributeType (int attributeRank);
	
	/** The length of the tuples of this type.
	 * 
	 * @return
	 */
	public int getTupleLength ();
	
	/** The offset where a given column starts in a byte array that stores a tuple of this type.
	 * 
	 * @param columnRank
	 * @return
	 */
	public int getColumnOffset (int columnRank);
	
}
