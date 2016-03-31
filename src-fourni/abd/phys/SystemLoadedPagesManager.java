package abd.phys;

import java.io.IOException;

/** Manages the usage of the main memory by the SGBD.
 * Responsible for loading and saving pages from/to disk.
 * Manages a fixed finite number of page buffers, that are offered on demand for read only or read/write access. 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface SystemLoadedPagesManager {

	/** Loads a page in memory.
	 * If the page is loaded w/o write access, then modifications on this page will not be written on disk.
	 * 
	 * @param relationName
	 * @param pageNumber
	 * @param writeAccess whether the page allows write access
	 * @return
	 * @throws IOException
	 */
	public LoadedPage loadPage(String relationName, int pageNumber, boolean writeAccess) throws IOException;

	/** Indicates that a page is released (not in use any more).
	 * If the page was loaded for write access, then it is written on disk.
	 * 
	 * @param loadedPage The page to be released. Must be produced by a previous call of {@link #loadPage(String, int, boolean)} or {@link #loadAsNewPage(String, int, boolean)}. An error occurs otherwise.
	 * @throws IOException
	 */
	public void releasePage(LoadedPage loadedPage) throws IOException;

	/** Creates a new page on disk and loads it.
	 * The page's buffer is initialized to 0's. It is the responibility of the user to initialize it as a correct page.
	 * 
	 * @param relationName
	 * @param pageNumber
	 * @param writeAccess
	 * @return
	 * @throws IOException
	 */
	public LoadedPage loadAsNewPage(String relationName, int pageNumber, boolean writeAccess) throws IOException;

}
