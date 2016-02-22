package abd.reldb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

import reldb.DBTableImpl;

import abd.Factory;

public class TestDBTableWithOneIndex extends TestDBTable {
	// The table with index should pass all the tests for the table without index
	
	// This method allows to simulate the index for the tests w/o index, by taking arbitrarily the first column for the index
	protected DBTable getTableFor (Path dataFolder, TableDescription tableDescr, Object...args) throws IOException {
		int indexColumnRank;
		if (args.length == 0)
			indexColumnRank = 0;
		else
			indexColumnRank = (int) args[0];
		return Factory.newDBTableWithIndexOnOneColumn(dataFolder, tableDescr, indexColumnRank);
	}
	
	@Test
	public void testAddRetrieveWithSeveralPages2() throws IOException {
		
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Create tuples
		byte[][] addedTuples = new byte[20][];
		for (int i = 0; i < addedTuples.length; i++) {
			addedTuples[i] = new byte[240];
			Arrays.fill(addedTuples[i], 0, 240, (byte)('a'+i));
		}
		
		// Make some of them have the same key
		for (int i = 0; i < addedTuples.length; i++) {
			if (i%2 == 0)
				Arrays.fill(addedTuples[i], 0, 10, (byte)('0'+(i/5)));
			else 
				Arrays.fill(addedTuples[i], 0, 10, (byte)('0'+(i/8 + 3)));
		}
		
		// Create 3 more tuples that are not added
		byte[][] otherTuples = new byte[3][];
		for (int i = 0; i < otherTuples.length; i++) {
			otherTuples[i] = new byte[240];
			Arrays.fill(otherTuples[i], 0, 240, (byte)('p'+i));
		}
				
		// Initialize
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {

			// add the tuples
			for (int i = 0; i < addedTuples.length; i++) {
				dbimp.addTuple(addedTuples[i]);
			}
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {
			
			assertTrue(dbimp.existsTuple(addedTuples[0]));
			assertTrue(dbimp.existsTuple(addedTuples[9]));
			assertFalse(dbimp.existsTuple(otherTuples[0]));
			assertTrue(dbimp.existsTuple(addedTuples[1]));
			
		}
	}
	
	@Test
	public void testAddDelete() throws IOException {
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Create tuples
		byte[][] addedTuples = new byte[18][];
		for (int i = 0; i < addedTuples.length; i++) {
			addedTuples[i] = new byte[240];
			Arrays.fill(addedTuples[i], 0, 240, (byte)('a'+i));
		}
		
		// Make some of them have the same key
		for (int i = 0; i < addedTuples.length; i++) {
			Arrays.fill(addedTuples[i], 0, 10, (byte)('0'+(i/6)));
		}
		
		// Add the tuples
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {

			// add the 10 first tuples
			for (int i = 0; i < addedTuples.length; i++) {
				dbimp.addTuple(addedTuples[i]);
			}	
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		// Remove one tuple for every key
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {
			dbimp.deleteTuple(addedTuples[0]);
			dbimp.deleteTuple(addedTuples[6]);
			dbimp.deleteTuple(addedTuples[12]);
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		// add them again in different order
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {

			dbimp.addTuple(addedTuples[12]);
			dbimp.addTuple(addedTuples[0]);
			dbimp.addTuple(addedTuples[6]);

			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
	}
	
}
