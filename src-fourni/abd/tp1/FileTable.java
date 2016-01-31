package abd.tp1;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;


/** An interface for very basic operations on a database table like structure stored on disk.
 * A database table is supposed to have a positive arity (given at construction time), and as many columns which values are Strings.
 * Valid column ranks are in the range {@code 0 .. getArity()-1}.
 * A table can contain 0 rows.
 * Arity cannot be 0.
 * 
 * Modifying the table is only possible by adding a tuple to it, see {@link #addTuple(String...)}
 * Inspecting the contents of the table is only possible through an iterator, see {@link #tuplesIterator()}.
 * There can be several iterators active simultaneously on the same table. 
 * Adding a tuple must invalidate (close) all currently active operators.
 *  
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 */
public interface FileTable extends Closeable {
	
	/** The arity of the table */
	public int getArity();
	
	/** The path of the file that stores the table. */
	public Path getPath();
	
	/** Adds a tuple to the table. 
	 * 
	 * @param tuple 
	 * @throws IllegalArgumentException if the tuple parameter does not have the right arity
	 * @throws IOException if an I/O exception occurred
	 */
	public void addTuple (String ... tuple) throws IllegalArgumentException, IOException;
	
	/** Iterator over all tuples that are currently in the table.
	 * 
	 * @return 
	 * @throws IOException if an I/O exception occurred during initialization of the iterator
	 */
	public TuplesIterator tuplesIterator () throws IOException;
	
	/** Constructs a table that corresponds to a selection on this table.
	 * The selection predicate is given  
	 * The selected tuples are those which value at {@code columnRank} contains {@code contained} as substring.
	 * @param resultPath TODO
	 * @param contained The string defining the selection criterion.
	 * @param columnRank The column rank on which selection operates.
	 * @see String#contains(CharSequence)
	 * 
	 * @return
	 * @throws IllegalArgumentException if {@code columnRank} is not allowed by the arity
	 * @throws IOException if an I/O exception occurred
	 */
	public FileTable select (Path resultPath, String contained, int columnRank) throws IllegalArgumentException, IOException;
	
	/** Constructs a table that corresponds to a projection of this table.
	 * The result table contains only the columns which ranks are given.
	 * @param resultPath TODO
	 * @param columnRanks The column ranks on which projection operates.
	 * 
	 * @return
	 * @throws IllegalArgumentException if the {@code columnRanks} are not allowed by the arity
	 * @throws IOException if an I/O exception occurred
	 */
	public FileTable project (Path resultPath, int ... columnRanks) throws IllegalArgumentException, IOException;

	/** Closes all file readers and writers currently hold by this file table. 
	 * 
	 * @throws IOException  if an I/O exception occurred
	 */
	@Override
	public void close () throws IOException;
	
}
