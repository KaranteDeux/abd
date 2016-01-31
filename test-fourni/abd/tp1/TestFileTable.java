package abd.tp1;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import abd.Factory;

/** Tests an implementation of {@link FileTable}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 */
public class TestFileTable {

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir")+"/";
	
	private static final String TABLE_FILE = TMP_DIR + "table_file";
	private static final String RESULT_FILE = TMP_DIR + "result_file";
	
	@Before
	@After
	public void setUpTearDown () throws IOException {
		if (Files.exists(Paths.get(TABLE_FILE))) {
			Files.delete(Paths.get(TABLE_FILE));
		}
		if (Files.exists(Paths.get(RESULT_FILE))) {
			Files.delete(Paths.get(RESULT_FILE));
		}
	}
	
	@Test
	public void testArity() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 5)) {
			
			assertEquals(5, table.getArity());
		}

	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddTupleWrongArity1() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE),3)) {
		
			table.addTuple("aa", "bb", "cc", "dd");
			Factory.destroyFileTable(table);
		} 
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddTupleWrongArity2() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE),3)) {
		
			table.addTuple("aa", "bb");
		}
	}

	@Test
	public void testAddOneTupleIterate() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE),2)) {
			
			table.addTuple("aa", "bb");
			TuplesIterator it = table.tuplesIterator();
		
			assertTrue(it.hasNext());
		
			String[] tuple1exp = new String[]{"aa", "bb"};
			String[] tuple1 = it.next();
		
			assertArrayEquals(tuple1exp, tuple1);
		
			assertFalse(it.hasNext());
		}

	}
	
	@Test
	public void testNoTupleIterate () throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE),3)) {
			
			TuplesIterator it = table.tuplesIterator();
			assertFalse(it.hasNext());
		}
		
	}
	
	@Test
	public void testAddTwoTuplesIterate() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) {
			
			table.addTuple("aaa", "bb");
			table.addTuple("cc", "ddd");
			TuplesIterator it = table.tuplesIterator();

			assertTrue(it.hasNext());

			String[] tuple1 = it.next();

			assertTrue(it.hasNext());

			String[] tuple2 = it.next();

			// Test the value of the tuples
			assertTrue("aaa".equals(tuple1[0]) || "aaa".equals(tuple2[0]));

			// The two tuples are different
			assertFalse(tuple1[1].equals(tuple2[1])); 

			assertFalse(it.hasNext());
		}
	}
	
	@Test
	public void testAlternateReadWrite() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) {
			table.addTuple("1aa1", "bb");
			table.addTuple("2222", "ccc");

			TuplesIterator it = table.tuplesIterator();
			String[] tuple1 = it.next();
			String[] tuple2 = it.next();
			assertFalse(it.hasNext());

			assertTrue("bb".equals(tuple1[1]) || "bb".equals(tuple2[1]));
			assertTrue("ccc".equals(tuple1[1]) || "ccc".equals(tuple2[1]));

			table.addTuple("3aa3", "dddd");
			table.addTuple("4a44", "eeeee");

			it = table.tuplesIterator();
			tuple1 = it.next();
			tuple2 = it.next();
			String[] tuple3 = it.next();
			String[] tuple4 = it.next();
			assertFalse(it.hasNext());

			assertTrue("bb".equals(tuple1[1]) || "bb".equals(tuple2[1]) || "bb".equals(tuple3[1]) || "bb".equals(tuple4[1]));
			assertTrue("ccc".equals(tuple1[1]) || "ccc".equals(tuple2[1]) || "ccc".equals(tuple3[1]) || "ccc".equals(tuple4[1]));
			assertTrue("dddd".equals(tuple1[1]) || "dddd".equals(tuple2[1]) || "dddd".equals(tuple3[1]) || "dddd".equals(tuple4[1]));
			assertTrue("eeeee".equals(tuple1[1]) || "eeeee".equals(tuple2[1]) || "eeeee".equals(tuple3[1]) || "eeeee".equals(tuple4[1]));
		}
	}
	
	@Test
	public void testTwoSuccessiveReads() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) {
			table.addTuple("1aa1", "bb");
			table.addTuple("2222", "ccc");

			TuplesIterator it = table.tuplesIterator();
			String[] tuple1 = it.next();
			String[] tuple2 = it.next();
			assertFalse(it.hasNext());

			it = table.tuplesIterator();
			tuple1 = it.next();
			tuple2 = it.next();
			assertFalse(it.hasNext());

			assertTrue("bb".equals(tuple1[1]) || "bb".equals(tuple2[1]));
			assertTrue("ccc".equals(tuple1[1]) || "ccc".equals(tuple2[1]));
		}
	}
	
	@Test
	public void testConcurrentReads() throws IOException {
		
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) {
			
			table.addTuple("aa", "11");
			table.addTuple("bb", "22");
			table.addTuple("cc", "33");
			
			TuplesIterator it1 = table.tuplesIterator();
			it1.next();
			TuplesIterator it2 = table.tuplesIterator();
			it2.next();
			it2.next();
			it2.next();
			assertFalse(it2.hasNext());
			it1.next();
			it1.next();
			assertFalse(it1.hasNext());
		}
	}
	
	@Test
	public void testAddInvalidatesIterator() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) {
			
			table.addTuple("aa", "11");
			table.addTuple("bb", "22");
			table.addTuple("cc", "33");
			
			TuplesIterator it = table.tuplesIterator();
			it.next();
			table.addTuple("dd", "44");
			try {
				it.next(); 
				fail("Iterator should be invalidated by addTuple");
			} catch (Exception e) {
				
			}
		}
	}
	
	@Test
	public void testCloseOnIteratorInvalidatesIterator() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 3)) {
			
			table.addTuple("1aa1", "bb", "XXX");
			table.addTuple("2222", "cc", "YYY");
			table.addTuple("3aa3", "dd", "ZZZ");
			
			TuplesIterator it = table.tuplesIterator();
			it.next();
			it.close();
			try {
				it.next(); 
				fail("Iterator should be invalidated after close.");
			} catch (Exception e) {
				
			}
		}		
	}

	@Test
	public void closeOnTableInvalidatesIterator() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 3)) {
			
			table.addTuple("1aa1", "bb", "XXX");
			table.addTuple("2222", "cc", "YYY");
			table.addTuple("3aa3", "dd", "ZZZ");
			
			TuplesIterator it = table.tuplesIterator();
			it.next();
			table.close();
			try {
				it.next(); 
				fail("Iterator should be invalidated after close.");
			} catch (Exception e) {
				
			}
		}		
		
	}
	
	
	@Test
	public void testSelect1() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2)) 
		{
			table.addTuple("1aa1", "bb");
			table.addTuple("2222", "ccc");
			table.addTuple("3aa3", "dddd");
			table.addTuple("4a44", "eeeee");
		
			try (FileTable selectResult = table.select(Paths.get(RESULT_FILE), "aa", 0))
			{
				assertEquals(2, selectResult.getArity());
				TuplesIterator it = selectResult.tuplesIterator();
		
				String[] tuple1 = it.next();
				String[] tuple2 = it.next();
				assertFalse(it.hasNext());
		
				assertTrue("bb".equals(tuple1[1]) || "bb".equals(tuple2[1])); 
				assertTrue("dddd".equals(tuple1[1]) || "dddd".equals(tuple2[1]));
				assertNotEquals(tuple1[1], tuple2[1]);
			}
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSelectIllegalColumnRank() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 2))
		{
			table.addTuple("1aa1", "b");
			table.addTuple("2222", "cc");
			table.select(Paths.get(TMP_DIR + "table_testSelect2Result"), "aa", 2); 	// IllegalArgumentException
		}
	}

	@Test
	public void testProject1() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 3)) {


			table.addTuple("1aa1", "bbb", "XXX");
			table.addTuple("2222", "cc", "YY");
			table.addTuple("3aa3", "d", "Z");

			try (FileTable projectResult = table.project(Paths.get(RESULT_FILE),1, 2)) {
				assertEquals(2, projectResult.getArity());

				TuplesIterator it = projectResult.tuplesIterator();

				String[] tuple1 = it.next();
				String[] tuple2 = it.next();
				String[] tuple3 = it.next();
				assertFalse(it.hasNext());

				assertTrue("bbb".equals(tuple1[0]) || "bbb".equals(tuple2[0]) || "bbb".equals(tuple3[0])); 
				assertTrue("cc".equals(tuple1[0]) || "cc".equals(tuple2[0]) || "cc".equals(tuple3[0]));
				assertTrue("d".equals(tuple1[0]) || "d".equals(tuple2[0]) || "d".equals(tuple3[0]));

				assertTrue("XXX".equals(tuple1[1]) || "XXX".equals(tuple2[1]) || "XXX".equals(tuple3[1])); 
				assertTrue("YY".equals(tuple1[1]) || "YY".equals(tuple2[1]) || "YY".equals(tuple3[1]));
				assertTrue("Z".equals(tuple1[1]) || "Z".equals(tuple2[1]) || "Z".equals(tuple3[1]));

				assertNotEquals(tuple1[0], tuple2[0]);
				assertNotEquals(tuple1[0], tuple3[0]);
				assertNotEquals(tuple2[0], tuple3[0]);
			}
		}
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testProjectZeroColumns() throws IOException {
		try (FileTable table = Factory.createFileTable(Paths.get(TABLE_FILE), 3)) {
	
			table.addTuple("1aa1", "bb", "XXX");
			table.addTuple("2222", "cc", "YYY");
			table.addTuple("3aa3", "dd", "ZZZ");
		
			table.project(Paths.get(RESULT_FILE));
		}
	}
	
		
	
}
