package abd.phys;

import java.io.IOException;

import abd.SGBD;

/** Manages the usage of memory by index structures. 
 * A limited amount of memory is available, fixed by {@link SGBD#TOTAL_NB_INDEX_PAGES}.
 * If too many or too big indexes are loaded, a {@link NoFreeMemoryException} exception is thrown.
 * 
 * Supports only in-memory indexes
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface SystemLoadedIndexesManager {
	
	/** Loads an in-memory index.
	 * Only one user can require write access at a time. 
	 * An exception is thrown if there is an attempt to load an index with write access if it is already in use, or to load an index for read access if it is already used for write access. 
	 * 
	 * @param relationName
	 * @param indexColumnRank
	 * @param writeAccess
	 * @return
	 * @throws IOException
	 */
	public InMemoryIndex loadInMemoryIndex (String relationName, int indexColumnRank, boolean writeAccess) throws IOException;
	
	
	/** Provides a buffer for storing the index in main memory.
	 * The buffer has always the size of a page.
	 * @return
	 */
	
	public byte[] getFreePage();
	/** Indicates that a previously loaded in-memory index is not in use any more. 
	 * The index is discarded from memory only if there are no other concurrent users.
	 * 
	 * @param relationName
	 * @param indexColumnRank
	 * @throws IOException
	 */
	public void releaseInMemoryIndex (String relationName, int indexColumnRank) throws IOException;

}
