package abd.phys;

import java.io.IOException;
import java.nio.file.Path;

/** An index that is entirely loaded in memory before being used.
 * 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface InMemoryIndex extends Index {
	
	/** Writes the index to a file.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void writeToFile(Path fileName) throws IOException;
	
	/** Loads a previously saved index.
	 * 
	 * @param indexFileName
	 * @throws IOException
	 */
	public void loadFromFile(Path indexFileName) throws IOException;
	
	/** Indicates how many pages in main memory are currently used by this index structure.
	 * 
	 * @return
	 */
	public int memoryUsageNbPages();
	
	
}
