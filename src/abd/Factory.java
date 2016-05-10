package abd;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import abd.phys.DBTableForAdding;
import abd.phys.DefaultDBTableForAdding;
import abd.phys.DefaultInMemoryIndex;
import abd.phys.DefaultPageSequentialAccess;
import abd.phys.DefaultSystemLoadedIndexesManager;
import abd.phys.DefaultSystemLoadedPagesManager;
import abd.phys.FileTableImpl;
import abd.phys.HashFunction;
import abd.phys.Index;
import abd.phys.IndexType;
import abd.phys.MyHashFunctions;
import abd.phys.Page;
import abd.phys.SystemLoadedIndexesManager;
import abd.phys.SystemLoadedPagesManager;
import abd.ra.phys.DefaultRecordFilter;
import abd.ra.phys.PhysicalOperator;
import abd.ra.phys.RecordFilter;
import abd.ra.phys.TableMapIndexAccessOperator;
import abd.ra.phys.TableMapIndexedTraversalOperator;
import abd.ra.phys.TableSequentialAccessOperator;
import abd.schemas.DBTable;
import abd.schemas.Datatype;
import abd.schemas.TableDescription;
import abd.tp1.FileTable;


/** This class contains factory methods that provide instances of various interfaces.
 * 
 * @author Iovka Boneva
 *
 */
public class Factory {

	private static SystemLoadedPagesManager thePagesManager;
	
	/** The manager of loaded pages must be a singleton. */
	public static SystemLoadedPagesManager getPagesManager () {
		if (thePagesManager == null) {
			thePagesManager = new DefaultSystemLoadedPagesManager();
		}
		return thePagesManager;
	}

	private static SystemLoadedIndexesManager theIndexesManager;
	
	public static SystemLoadedIndexesManager getIndexesManager() {
		if (theIndexesManager == null) {
			theIndexesManager = new DefaultSystemLoadedIndexesManager();
		}
		return theIndexesManager;
	}
	
	/** Crée une page pour parcourir des données déjà stockées en mémoire. */
	public static Page newPage (ByteBuffer buffer, String relationName) {
		int recordSize = SGBD.getRelation(relationName).getTableDescription().getTupleLength();
		return new DefaultPageSequentialAccess(buffer, recordSize);
	}
	
	public static DBTableForAdding newDBTableForAdding (String relationName) throws IOException {
		return new DefaultDBTableForAdding(relationName);
	}
	
	public static HashFunction getHashFunction (Datatype datatype) {
		return MyHashFunctions.getDefaultHashFunction();
	}
	
	public static Index newIndex (String relationName, int columnRank) {
		int indexType = SGBD.getRelation(relationName).getIndexType(columnRank);
		if (indexType != IndexType.IN_MEMORY_MAP) return null;
		return new DefaultInMemoryIndex();
	}
	

	/** Creates a FileTable with given backup file and arity.
	 * The backup file should not exist prior to creation.
	 * 
	 * @param path
	 * @param arity
	 * @return
	 * @throws IOException if the given file exists, or other I/O error occurs
	 */
	public static FileTable createFileTable (Path path, int arity) throws IOException {
		Files.createFile(path);
		return new FileTableImpl(arity, path);	   
	}

	/** Destroys the backup file of a file table.
	 * Before destruction, closes all resources used by the file table.
	 * 
	 * @param fileTable
	 * @throws IOException if the given file exists, or other I/O error occurs
	 */
	public static void destroyFileTable (FileTable fileTable) throws IOException {
		if(!Files.exists(fileTable.getPath()))
			throw new NoSuchFileException("File does not exists");
		fileTable.close();
		Files.delete(fileTable.getPath());
	}

	public static DBTable newDBTableWithoutIndex (Path dataFolderPath, TableDescription tabledes ) throws IOException {
		return new DBTableImpl(dataFolderPath, tabledes);
	}

	public static DBTable newDBTableWithIndexOnOneColumn (Path dataFolderPath,  TableDescription tabledes, int IndexcolumnRank) throws IOException {
		return new DBTableImpl(dataFolderPath, tabledes, IndexcolumnRank);
	}

	public static DBTable newDBTableWithIndex (Path dataFolderPath, TableDescription tabledes, int... Index ) throws IOException {
		return new DBTableImpl(dataFolderPath, tabledes, Index);
	}

	public static PhysicalOperator newTableSequentialTraversalOperator(Path tableDataFolder, TableDescription tableDescription) { 
		return new DBTableImpl(tableDataFolder, tableDescription);
	}

	public static PhysicalOperator newTableMapIndexedTraversalOperator(Path tableDataFolder, TableDescription tableDescription, byte[] attributeValue, int indexedColumnRank) throws IOException{
		return new TableMapIndexAccessOperator(tableDataFolder, tableDescription, attributeValue, indexedColumnRank);
	}
	
	
	
	/** Returns an operator that iterates sequentially over all the tuples of the table.
	 * 
	 * @param relationName
	 * @return
	 * @throws IOException
	 */
	public static PhysicalOperator newTableSequentialTraversalOperator (String relationName) throws IOException {
		return	new TableSequentialAccessOperator(relationName);
	}
	
	/** Returns an iterator that takes advantage of an existing map index and iterates over only the tuples of the table that have a particular value in a particular column.
	 * 
	 * @param relationName 
	 * @param columnRank The column which value is being tested, should be a column that is indexed by a map index
	 * @param attributeValue The required value in the column
	 * @return
	 * @throws IOException
	 */
	public static PhysicalOperator newTableMapIndexedTraversalOperator (String relationName, int columnRank, byte[] attributeValue) throws IOException {
		return new TableMapIndexedTraversalOperator(relationName,
													SGBD.getRelation(relationName).getTableDescription(), attributeValue, columnRank);
	}

	/** Returns an empty record filter with no selectors and projectors.
	 * The empty filter can be specified by adding selectors and projectors.
	 * 
	 * @param input
	 * @param output
	 * @return
	 */
	public static RecordFilter newRecordFilter (TableDescription input, TableDescription output) {
		return new DefaultRecordFilter(input, output);
	}

	/** Returns a filter operator that consumes the output of a subOperator and filters them according to a filter.
	 * 
	 * @param subOp The sub-operator which output tuples are being consumed as input of this operator
	 * @param filter The filter applied to the consumed tuples
	 * @return
	 */
	public static PhysicalOperator newFilterOperator(PhysicalOperator subOperator, RecordFilter filter) {
		throw new UnsupportedOperationException("not yet implemented");
	}
	
}
