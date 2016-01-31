package abd.tp1;

import java.io.IOException;

/** Iterator which contents is backed up on a file, and which next and hasNext methods can raise an I/O exception.
 * 
 * @author Iovka Boneva
 *
 */

/**  Iterator which contents is stored in a file, so its {@code #next()} and @code {@link #hasNext()} methods can raise an I/O exception.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 */
public interface TuplesIterator {

	public boolean hasNext () throws IOException;
	public String[] next () throws IOException;
	
	/** Closes the iterator and frees all resources used by it.
	 * Any subsequent call to {@link #hasNext()} or {@link #next()} has undefined behavior.
	 * Closing an already closed iterator has no effect.
	 * 
	 * @throws IOException if an I/O error occurred during closing
	 */
	public void close () throws IOException;
}
