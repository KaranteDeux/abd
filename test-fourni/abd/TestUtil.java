package abd;


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Test;

import abd.schemas.AttributeType;
import abd.schemas.DefaultTableDescription;
import abd.schemas.TableDescription;

/** Utility methods and constants for testing.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 1 mars 2016
 */
public class TestUtil {

	public static final String TMP_DIR = System.getProperty("java.io.tmpdir")+"/"; 

	
	
	public static String char2_char2_table_name = "CHAR2CHAR2";
	public static TableDescription descr_char2_char2 = 
			new DefaultTableDescription(char2_char2_table_name, AttributeType.newCharacter(2), AttributeType.newCharacter(2));
	
	public static String varchar3_varchar3_table_name = "VARCHAR3VARCHAR3";
	public static TableDescription descr_varchar3_varchar3 = 		
			new DefaultTableDescription(varchar3_varchar3_table_name, AttributeType.newVarchar(3), AttributeType.newVarchar(3));

	public static String char120_char120_table_name = "CHAR120CHAR120";
	public static TableDescription descr_char120_char120 = 
			new DefaultTableDescription(char120_char120_table_name, AttributeType.newCharacter(120), AttributeType.newCharacter(120));

	public static String char10_char230_table_name = "CHAR10CHAR230";
	public static TableDescription descr_char10_char230 =
			new DefaultTableDescription(char10_char230_table_name, AttributeType.newCharacter(10), AttributeType.newCharacter(230));

	
	public static Path getPathInTmpDir (String fileName) {
		return Paths.get(TMP_DIR + "/" + fileName);
	}
	
	
	public static void recursiveRemoveFolder (Path folderPath) throws IOException {
		
		if (! folderPath.startsWith(Paths.get(TMP_DIR))) {
			throw new IllegalArgumentException("Refuse to delete a folder that is not in the system tmpdir");
		}
		
		Files.walkFileTree(folderPath, new FileVisitor<Path>() {
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				System.out.println("Deleting directory: "+ dir);
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				System.out.println("Deleting file: "+file);
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				System.out.println(exc.toString());
				return FileVisitResult.CONTINUE;
			}
		});
	}
	
	
	@Test
	public void testRecursiveDeleteAllowedInTmpFolder() throws IOException {
		Path folder = Paths.get(TMP_DIR + "/todelete");
		Path file1 = Paths.get(folder.toString() + "/file1");
		Path file2 = Paths.get(folder.toString() + "/file2");
		Files.createDirectory(folder);
		Files.createFile(file1);
		Files.createFile(file2);
		
		recursiveRemoveFolder(folder);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testRecursiveDeleteNotAllowedOutsideTmpFolder() throws IOException {
		Path folder = Paths.get("todelete");
		Path file1 = Paths.get(folder.toString() + "/file1");
		Path file2 = Paths.get(folder.toString() + "/file2");
		Files.createDirectory(folder);
		Files.createFile(file1);
		Files.createFile(file2);
		
		recursiveRemoveFolder(folder);
	}
}
