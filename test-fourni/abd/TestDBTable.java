package abd;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

import abd.schemas.DBTable;
import abd.schemas.TableDescription;

public class TestDBTable {
	
	
	protected DBTable getTableFor (Path dataFolder, TableDescription tableDescr, Object... arg) throws IOException {
		return Factory.newDBTableWithoutIndex(dataFolder, tableDescr);
	}
	
	@Test
	public void testAddRetrieveOneTuple() throws IOException {
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithOnePage");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Initialize
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char2_char2)) {
			
			// tuple (aa, 11)
			byte[] tuple1toAdd = new byte[]{'a','a','1','1'};
			dbimp.addTuple(tuple1toAdd);
			
			assertTrue(dbimp.existsTuple(tuple1toAdd));
		}	
	}
	
	@Test
	public void testAddRetrieveWithSeveralPages() throws IOException {
		
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Create 11 tuples, every tuple contains a unique letter from 'a' to 'k' (i.e. tuples[0] contains the character 'a' only)
		byte[][] tuples = new byte[11][];
		for (int i = 0; i < tuples.length; i++) {
			tuples[i] = new byte[240];
			Arrays.fill(tuples[i], 0, 240, (byte)('a'+i));
		}
		
		// Initialize
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char120_char120)) {

			// add the 10 first tuples
			for (int i = 0; i < tuples.length-1; i++) {
				dbimp.addTuple(tuples[i]);
			}
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char120_char120)) {
			
			assertTrue(dbimp.existsTuple(tuples[0]));
			assertTrue(dbimp.existsTuple(tuples[9]));
			assertFalse(dbimp.existsTuple(tuples[10]));
			assertTrue(dbimp.existsTuple(tuples[1]));
			
			//dbimp.printContents();
		}	
	}
	
	@Test
	public void testDeleting() throws IOException {
		
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Create 10 tuples, every tuple contains a unique letter from 'a' to 'j' (i.e. tuples[0] contains the character 'a' only)
		byte[][] addedTuples = new byte[10][];
		for (int i = 0; i < addedTuples.length; i++) {
			addedTuples[i] = new byte[240];
			Arrays.fill(addedTuples[i], 0, 240, (byte)('a'+i));
		}
		// Create 3 more tuples that are not added additionally
		byte[][] otherTuples = new byte[3][];
		for (int i = 0; i < otherTuples.length; i++) {
			otherTuples[i] = new byte[240];
			Arrays.fill(otherTuples[i], 0, 240, (byte)('p'+i));
		}
		
		// Initialize
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char120_char120)) {

			// add the 10 first tuples
			for (byte[] tuple: addedTuples) {
				dbimp.addTuple(tuple);
			}	
		}
		

		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char120_char120)) {
		
			// delete tuple that is not there
			dbimp.deleteTuple(otherTuples[0]);
			for (int i = 0; i < addedTuples.length; i++) {
				assertTrue(dbimp.existsTuple(addedTuples[i]));
			}
			

			// delete two tuples that are present
			dbimp.deleteTuple(addedTuples[1]);
			dbimp.deleteTuple(addedTuples[9]);
		
			assertFalse(dbimp.existsTuple(addedTuples[1]));
			assertFalse(dbimp.existsTuple(addedTuples[9]));
			
			for (int i = 0; i < 10; i++) {
				if (i != 1 && i != 9) {
					assertTrue(dbimp.existsTuple(addedTuples[i]));
				}
			}
		}	
	}
	
	@Test
	public void testGetTupleBySelection() throws IOException {
		
		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);
		
		// Create tuples and values of the attributes
		byte[][] addedTuples = new byte[10][];
		byte[][] firstAttribute = new byte[10][];
		for (int i = 0; i < addedTuples.length; i++) {
			addedTuples[i] = new byte[240];
			Arrays.fill(addedTuples[i], 0, 10, (byte)('a'+i));
			Arrays.fill(addedTuples[i], 10, 240, (byte)('0'+i));
			
			firstAttribute[i] = new byte[10];
			Arrays.fill(firstAttribute[i], 0, 10, (byte)('a'+i));
		}
		
		// Create 3 more attribute values that are not added
		byte[][] otherAttributeValues = new byte[3][];
		for (int i = 0; i < otherAttributeValues.length; i++) {
			otherAttributeValues[i] = new byte[10];
			Arrays.fill(otherAttributeValues[i], 0, 10, (byte)('x'+i));
		}
				
		// Initialize
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {

			// add the tuples
			for (int i = 0; i < addedTuples.length; i++) {
				dbimp.addTuple(addedTuples[i]);
			}
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		// Selection
		try (DBTable dbimp = getTableFor(dataFolder, TestUtil.descr_char10_char230, 0)) {

			byte[] tuple;
			
			tuple = dbimp.getTupleBySelection(0, firstAttribute[1]);
			// Check this is the right tuple
			assertNotNull(tuple);
			assertArrayEquals(addedTuples[1], tuple);
			
			tuple = dbimp.getTupleBySelection(0, firstAttribute[4]);
			assertNotNull(tuple);
			assertArrayEquals(addedTuples[4], tuple);

			// Look for a non existing tuple
			tuple = dbimp.getTupleBySelection(0, otherAttributeValues[0]);
			assertNull(tuple);
		}
		
	}
}
