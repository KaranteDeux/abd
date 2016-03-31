package abd.phys;

import java.io.Closeable;
import java.io.IOException;

/** Allows to use a database table for adding tuples only.
 * Allows to add new tuples to an existing table or to an empty table.
 * No other operations are supported.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 26 févr. 2016
 */
public interface DBTableForAdding extends Closeable {

	public void addTuple(byte[] tuple) throws IOException ;
	
}
