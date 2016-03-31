package abd.schemas;

/** A relation represents the schema information, including the indexes and their types.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 1 mars 2016
 */
public interface DBRelation {
	
	public String getName();

	public TableDescription getTableDescription();

	public int[] getIndexColumnRanks();
	
	public int getIndexType (int columnRank);
}
