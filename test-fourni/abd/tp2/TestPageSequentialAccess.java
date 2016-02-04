package abd.tp2;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import tp2.DefaultPageSequentialAccess;

/**
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 28 janv. 2016
 */
public class TestPageSequentialAccess {

	// -------------------------------------------------
	// README UTILISATION DE LA CLASSE DE TEST
	// ------------------------------------------------- 
	// On suppose ici que l'implementation de l'interface s'appelle DefaultPageSequentialAccess
	// Vous devrez utiliser votre classe à la place.
	// Le constructeur doit correspondre
	// De plus, on a besoin d'une méthode 
	// void initPage()
	// qui réinitialise la page pour qu'elle corresponde à une page vide avec 0 enregistrements
	// Cette initialisation dépendra de l'organisation logique de la page que vous aurez choisie
	// 
	// L'implémentation des méthodes de suppression et de modification n'est pas obligatoire à ce niveau.
	// Elle le sera plus tard en adéquation avec la gestion de la table que vous aurez implémentée.
	// Ici on prend une vision où on ne peut supprimer ou modifier que le dernier tuple
	// retourné par getNextRecord.
	// Vous pourrez choisir une autre façon de faire.
	// -------------------------------------------------
	
	
	
	// In all tests, we use tuples that contain the same character (byte) repeated all over 
	// Then every tuple is identified with its first byte
	
	private ByteBuffer buffer = ByteBuffer.allocate(DefaultPageSequentialAccess.PAGE_SIZE_BYTES);
	
	@Before
	public void setUp () {
		//Arrays.fill(buffer.array(), (byte) 0);
	}
	
	@Test
	public void testSizeIsZeroAfterInitialization() {
		DefaultPageSequentialAccess page = new DefaultPageSequentialAccess(buffer, 20);
		page.initPage();
		assertEquals(0, page.getNbRecords());
	}
	
	@Test
	public void testAddCoherentWithNbRecords() {
		int size = 199;
		DefaultPageSequentialAccess page = new DefaultPageSequentialAccess(buffer, size);
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
		DefaultPageSequentialAccess page = new DefaultPageSequentialAccess(buffer, size);
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
	
	

	@Test
	public void testAddRemove() {
		
		int size = 5;
		DefaultPageSequentialAccess page = new DefaultPageSequentialAccess(buffer, size);
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
		
		System.out.println("remaining :" + page.getBuff().remaining());
		// 
		Set<Byte> initialTuples = new HashSet<>();
		initialTuples.add((byte)'a'); initialTuples.add((byte)'b'); initialTuples.add((byte)'c');

		byte removed_tuple;

		page.getNextRecord();
		removed_tuple = page.getNextRecord()[0]; // We remove the tuple that comes second in the iteration.
		System.out.println("remove tuple : " + (char) removed_tuple);	// We de not know which one it is, as there is no requirement on the order of iteration
		page.remove();
		
		System.out.println("remaining :" + page.getBuff().remaining());

		
		assertEquals(2, page.getNbRecords()); 
		

		page.resetPosition();
		Set<Byte> remainingTuples = new HashSet<>();
		byte x;
		
		x = page.getNextRecord()[0];
		System.out.println("1 + " + (char)x);
		remainingTuples.add(x);
		x = page.getNextRecord()[0];
		System.out.println("2 + " + (char)x);
		remainingTuples.add(x);
		
		
		byte [] array = page.getNextRecord();
		for(byte b : array){
			System.out.print(b);
		}
		System.out.println();
		
		assertNull(array); // No more records
		
		assertFalse(remainingTuples.contains(removed_tuple));
		
		Set<Byte> shouldBeSameAsInitial = new HashSet<>();
		shouldBeSameAsInitial.addAll(remainingTuples);
		shouldBeSameAsInitial.add(removed_tuple);
		
		assertEquals(initialTuples, shouldBeSameAsInitial);	
	}
	
	
	
	@Test
	public void testSetRecord() {
		int size = 5;
		DefaultPageSequentialAccess page = new DefaultPageSequentialAccess(buffer, size);
		page.initPage();

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
		
		// Modify the 2nd tuple
		page.getNextRecord();
		byte modified_tuple = page.getNextRecord()[0];
		
		byte[] newTuple = new byte[size];
		Arrays.fill(newTuple, (byte) 'x');
		page.setRecord(newTuple);
		
		// Verify that the modified tuple was removed by the new tuple
		page.resetPosition();
		byte[] currentTuple;
		
		Set<Byte> tuples_that_are_present = new HashSet<>();
		while ((currentTuple = page.getNextRecord()) != null) {
			if (currentTuple[0] == modified_tuple)
				fail("This tuple should have been removed");
			else 
				tuples_that_are_present.add(currentTuple[0]);
		}
		
		Set<Byte> expectedTuples = new HashSet<>();
		expectedTuples.add((byte)'a');
		expectedTuples.add((byte)'b');
		expectedTuples.add((byte)'c');
		expectedTuples.remove(modified_tuple);
		expectedTuples.add((byte)'x');
		
		assertEquals(expectedTuples, tuples_that_are_present);
		
	}
	
	
}