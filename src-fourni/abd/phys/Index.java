package abd.phys;

import java.io.IOException;
import java.util.Iterator;

/** Generic interface for index structures.
 * Allows to add, remove and retrieve key-pageNumber associations.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface Index {

	/** Adds a new key-pageNumber association, indicating that values with the given key can be found on the given page.
	 * 
	 * @param key
	 * @param pageNumber
	 * @throws IOException
	 */
	public void putPageNumberForKey(int key, int pageNumber) throws IOException ;
	
	/** Removes a key-pageNumber association.
	 * 
	 * @param key
	 * @param pageNumber
	 * @throws IOException
	 */
	public void removePageNumberForKey(int key, int pageNumber) throws IOException;
	
	/** An iterator over all key-pageNumber associations for a given key 
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public Iterator<Integer> pagesIterator(int key) throws IOException;
	
	/** The type of the index, this is one among the types defined in {@link IndexType}.
	 * 
	 * @return
	 */
	public int getIndexType();

}
