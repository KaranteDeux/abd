package abd.ra.phys;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import org.junit.Test;

import abd.Factory;
import abd.tp1.FileTable;

/** Tests {@link Factory}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 */
public class TestFactory_FileTable {
	
	private static final String TMP_DIR = System.getProperty("java.io.tmpdir")+"/";
	
	@Test
	public void testCreateFileTable() throws IOException {
		if (Files.exists(Paths.get(TMP_DIR + "abc"))) {
			Files.delete(Paths.get(TMP_DIR + "abc"));
		}
		assertFalse(Files.exists(Paths.get(TMP_DIR + "abc")));
		Factory.createFileTable(Paths.get(TMP_DIR + "abc"), 2);
		assertTrue(Files.exists(Paths.get(TMP_DIR + "abc")));
	}
	
	@Test
	public void testCreateDestroyFileTable() throws IOException {
		if (Files.exists(Paths.get(TMP_DIR + "abc"))) {
			Files.delete(Paths.get(TMP_DIR + "abc"));
		}
		
		FileTable table = Factory.createFileTable(Paths.get(TMP_DIR + "abc"), 2);
		assertTrue(Files.exists(Paths.get(TMP_DIR + "abc")));
		Factory.destroyFileTable(table);
		assertFalse(Files.exists(Paths.get(TMP_DIR + "abc")));
	}
	
	@Test (expected=NoSuchFileException.class)
	public void testDestroyNonExistingFileTable() throws IOException {
		if (Files.exists(Paths.get(TMP_DIR + "abc"))) {
			Files.delete(Paths.get(TMP_DIR + "abc"));
		}

		FileTable table = Factory.createFileTable(Paths.get(TMP_DIR + "abc"), 2);
		assertTrue(Files.exists(Paths.get(TMP_DIR + "abc")));
		Factory.destroyFileTable(table);
		assertFalse(Files.exists(Paths.get(TMP_DIR + "abc")));
		
		Factory.destroyFileTable(table);
	}
	
}
