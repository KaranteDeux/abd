package abd.phys;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import abd.Factory;
import abd.SGBD;

/** For testing an implementation of {@link Page}
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 28 janv. 2016
 */
public class TestPageSequentialAccess {

	
	// In all tests, we use tuples that contain the same character (byte) repeated all over 
	// Then every tuple is identified with its first byte
	
	private ByteBuffer buffer = ByteBuffer.allocate(SGBD.PAGE_SIZE);
	
	@Before
	public void setUp () {
		Arrays.fill(buffer.array(), (byte) 0);
	}
	
	@Test
	public void testSizeIsZeroAfterInitialization() {
		Page page = new DefaultPageSequentialAccess(buffer, 20);
		page.initPage();
		assertEquals(0, page.getNbRecords());
	}
	
	@Test
	public void testAddCoherentWithNbRecords() {
		int size = 199;
		Page page = new DefaultPageSequentialAccess(buffer, size);
		page.initPage();
		
		boolean added;
		
		assertEquals(0, page.getNbRecords());
		
		byte[] a_tuple = new byte[size];
		Arrays.fill(a_tuple, (byte) 'a');
		added = page.addRecord(a_tuple);
		assertTrue(added);
		
		assertEquals(1, page.getNbRecords());
		
		byte[] b_tuple = new byte[size];
		Arrays.fill(b_tuple, (byte) 'b');
		page.addRecord(b_tuple);
		assertTrue(added);
		
		assertEquals(2, page.getNbRecords());
	}
	
	
	@Test
	public void testAddThenRetrieve() {
		int size = 190;
		Page page = new DefaultPageSequentialAccess(buffer, size);
		page.initPage();

		// Add three tuples 'a', 'b', 'c'
		byte[] a_tuple = new byte[size];
		Arrays.fill(a_tuple, (byte) 'a');
		byte[] b_tuple = new byte[size];
		Arrays.fill(b_tuple, (byte) 'b');
		byte[] c_tuple = new byte[size];
		Arrays.fill(c_tuple, (byte) 'c');
		
		page.addRecord(a_tuple);
		page.addRecord(b_tuple);
		page.addRecord(c_tuple);
		
		page.resetPosition();
		
		byte[] tuple1, tuple2, tuple3;
		tuple1 = page.getNextRecord();
		tuple2 = page.getNextRecord();
		tuple3 = page.getNextRecord();
		
		assertEquals("Retrieved tuple must has the same size as the added tuple", size, tuple1.length);
		assertEquals("Retrieved tuple must has the same size as the added tuple", size, tuple2.length);
		assertEquals("Retrieved tuple must has the same size as the added tuple", size, tuple3.length);
		
		// Declared for iteration purposes
		byte[][] theThreeTuples = new byte[][]{tuple1, tuple2, tuple3};

		Set<Byte> expectedTuples = new HashSet<>();
		expectedTuples.add((byte)'a'); expectedTuples.add((byte)'b'); expectedTuples.add((byte)'c');
		
		Set<Byte> retrievedTuples = new HashSet<>();
		
		for (byte[] oneTuple : theThreeTuples) {
			for (int i = 1; i < size; i++) {
				if (oneTuple[0] != oneTuple[i]) {
					// Checks that the retrived tuple contains the same character all over, thus 
					// ensuring that tuples are not mixed with each other
					fail("A retrieved tuple should be equal to an added tuple");
				}
			}
			
			retrievedTuples.add(oneTuple[0]);
		}
		assertEquals(expectedTuples, retrievedTuples);
	}
	
}
