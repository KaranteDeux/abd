package abd.ra.phys;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import abd.Factory;
import abd.TestUtil;
import abd.schemas.DBTable;

public class TestTableOperators {

	@Test
	public void testSequential() throws IOException {
		// Create a DBTable with few tuples
		
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
		
		// Add the tuples
		try (DBTable dbimp = Factory.newDBTableWithoutIndex(dataFolder, TestUtil.descr_char120_char120)) {

			// add the 10 first tuples
			for (int i = 0; i < tuples.length-1; i++) {
				dbimp.addTuple(tuples[i]);
			}
			
			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		
		// Iterate on the tuples
		try (PhysicalOperator seqAccess = Factory.newTableSequentialTraversalOperator(dataFolder,TestUtil.descr_char120_char120)) {
			byte[] record;
			while ((record = seqAccess.nextRecord()) != null) {
				System.out.println((char) record[0]);
			}
		}
	}
	
	
	@Test
	public void testIndex() throws IOException {
		// Create a DBTable with few tuples

		Path dataFolder = Paths.get(TestUtil.TMP_DIR + "/tableWithSeveralPages");
		// Create the empty folder
		TestUtil.recursiveRemoveFolder(dataFolder);
		Files.createDirectory(dataFolder);

		// Create 20 tuples, every tuple contains a unique letter from 'a' to to 'a'+20 in the second column
		// and every third tuple contains '1' only in the first column
		byte[][] tuples = new byte[20][];
		for (int i = 0; i < tuples.length; i++) {
			tuples[i] = new byte[240];
			Arrays.fill(tuples[i], 0, 240, (byte)('a'+i));
			if (i % 3 == 1) {
				Arrays.fill(tuples[i], 0, 10, (byte)('1'));
			}
		}

		// Add the tuples
		try (DBTable dbimp = Factory.newDBTableWithIndexOnOneColumn(dataFolder, TestUtil.descr_char10_char230, 0)) {

			// add the 20 first tuples
			for (int i = 0; i < tuples.length; i++) {
				System.out.println("Adding " + ((char) tuples[i][0]) + ((char) tuples[i][239]));
				dbimp.addTuple(tuples[i]);
			}

			//((MyDBTableWithoutIndex) dbimp).printContents();
		}
		
		//printIndex(dataFolder);
		
		// Iterate on the tuples
		byte[] value = new byte[10];
		Arrays.fill(value,  0, 10, (byte) '1');
		try (PhysicalOperator indexAccess = Factory.newTableMapIndexedTraversalOperator(dataFolder, TestUtil.descr_char10_char230, value, 0)) {
			byte[] record;
			int cpt = 0;
			while ((record = indexAccess.nextRecord()) != null) {
				System.out.println("" + ((char) record[0]) + ((char) record[239]));
				if(cpt > 20)
					System.exit(0);
				cpt++;
			}
		}

	}

	
	
	public void printIndex(Path dataFolder) throws IOException {
		ByteBuffer buffer = null;
		Path fileName = Paths.get(dataFolder.toString() + "/INDEX0"); 
		try (FileChannel fc = FileChannel.open(fileName, StandardOpenOption.READ)) {
			if (fc.size() > Integer.MAX_VALUE)
				throw new RuntimeException("File is too big to be read. Aborting");
			buffer = ByteBuffer.allocate((int) fc.size());
			fc.read(buffer);
			
			buffer.rewind();
			int maxKeyValue = buffer.getInt();
			System.out.println("Max key value: " + maxKeyValue);
			
			while (buffer.hasRemaining()) {
				int key = buffer.getInt();
				int nbValues = buffer.getInt();
				Set<Integer> list = new HashSet<>(nbValues+2);
				for (int i = 0; i < nbValues; i++) {
					list.add(buffer.getInt());
				}
				
				System.out.print(String.format("Key : %d; nbVales: %d", key, nbValues));
				System.out.println(" Values: " + list);
			}
		}
		
	}
}
