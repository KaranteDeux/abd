package abd.phys;

import abd.SGBD;

/** Enumerates the index types supported by the implementation.
 * Such index types are to be used by {@link SGBD#createTable(String, abd.schemas.AttributeType[], int[], int[])}
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public class IndexType {
	
	public static final int IN_MEMORY_MAP = 0;
	public static final int B_TREE = 1;
	public static final int SORTED = 2;
	public static final int BITMAP = 3;
	
}
