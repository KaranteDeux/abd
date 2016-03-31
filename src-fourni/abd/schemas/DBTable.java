package abd.schemas;

import java.io.Closeable;
import java.io.IOException;

import abd.ra.phys.PhysicalOperator;

/** A database table, that allows to access the data.
 * 
 * @author Iovka Boneva
 * @author Pierre Bourhis
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 15 janv. 2016
 */
public interface DBTable extends Closeable, PhysicalOperator{

	/** Retrieves the table description of the table.
	 * 
	 * @return
	 */
	public TableDescription getTableDescription();
	
	
	//
	// index
	// fichiers
	// pages
	
	
	public void addTuple (byte[] tupleValue) throws IOException;
	
	public boolean existsTuple (byte[] tupleValue) throws IOException;
	
	public byte[] getTupleBySelection (int columnRank, byte[] attributeValue) throws IOException;
	

	public void deleteTuple (byte[] TupleValue) throws IOException;
	
	
	
	@Override
	public void close() throws IOException;
	public void open() throws IOException;
	
	
}
