package abd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import reldb.DBTableImpl;
import tp2.FileTableImpl;
import abd.reldb.DBTable;
import abd.reldb.TableDescription;
import abd.tp1.FileTable;

/** This class contains factory methods that provide instances of various interfaces.
 * 
 * @author Iovka Boneva
 *
 */
public class Factory {

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
	


}
