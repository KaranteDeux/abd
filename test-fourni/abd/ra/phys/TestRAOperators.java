package abd.ra.phys;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import abd.Factory;
import abd.SGBD;
import abd.phys.DBTableForAdding;
import abd.ra.SelectionCriterion_EqualityToConstant;
import abd.ra.phys.PhysicalOperator;
import abd.schemas.AttributeType;
import abd.schemas.DefaultTableDescription;
import abd.schemas.TableDescription;

public class TestRAOperators {

	@Test
	public void testTableSequentialAcess() throws IOException {
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();
		
		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{0}, new int[]{0});
		
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				dbtable.addTuple(tuple);
			}
		}
		SGBD.closeConnection();
		
		SGBD.openConnection();
		try (PhysicalOperator seqAccess = Factory.newTableSequentialTraversalOperator("TABLE_ONE")) {
			byte[] tuple;
			int nb = 0;
			Set<Character> firstChars = new HashSet<>();
			while ((tuple = seqAccess.nextRecord()) != null) {
				nb++;
				firstChars.add((char)(tuple[0]));
			}
			assertEquals(20, nb);
			for (int i = 0; i < 20; i++) {
				assertTrue(firstChars.contains((char)('a'+i)));
			}
		}
		SGBD.closeConnection();
	}
	
	@Test
	public void testTableIndexAccgess_KeysGrouped() throws IOException {
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();
		
		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{0}, new int[]{0});
		
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				if (i < 5) {
					Arrays.fill(tuple, 0, 120, (byte) '1');
				}
				dbtable.addTuple(tuple);
			}
		}
		SGBD.closeConnection();
		
		SGBD.openConnection();
		byte[] onesTuple = new byte[120];
		Arrays.fill(onesTuple, (byte)'1');
		try (PhysicalOperator indexAccess = Factory.newTableMapIndexedTraversalOperator("TABLE_ONE", 0, onesTuple)) {
			byte[] tuple;
			int nb = 0;
			Set<Character> lastChars = new HashSet<>();
			while ((tuple = indexAccess.nextRecord()) != null) {
				nb++;
				lastChars.add((char)(tuple[tuple.length-1]));
			}
			assertEquals(5, nb);
			for (int i = 0; i < 20; i++) {
				if (i < 5)
					assertTrue(lastChars.contains((char)('a'+i)));
			}
		}
		SGBD.closeConnection();
	}
	
	@Test
	public void testTableIndexAccgess_KeysSpread() throws IOException {
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();
		
		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{0}, new int[]{0});
		
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				if (i % 3 == 1) {
					Arrays.fill(tuple, 0, 120, (byte) '1');
				}
				dbtable.addTuple(tuple);
			}
		}
		SGBD.closeConnection();
		
		SGBD.openConnection();
		byte[] onesTuple = new byte[120];
		Arrays.fill(onesTuple, (byte)'1');
		try (PhysicalOperator indexAccess = Factory.newTableMapIndexedTraversalOperator("TABLE_ONE", 0, onesTuple)) {
			byte[] tuple;
			int nb = 0;
			Set<Character> lastChars = new HashSet<>();
			while ((tuple = indexAccess.nextRecord()) != null) {
				nb++;
				lastChars.add((char)(tuple[tuple.length-1]));
			}
			System.out.println(lastChars);
			assertEquals(7, nb);
			for (int i = 0; i < 21; i++) {
				if (i % 3 == 1)
					assertTrue(lastChars.contains((char)('a'+i)));
			}
		}
		SGBD.closeConnection();
	}

	@Test
	public void testFilterOperator() throws IOException {
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();
		
		AttributeType char10 = AttributeType.newCharacter(10);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char10, char10, char10, char10}, new int[]{0}, new int[]{0});		
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 200; i++) {
				Arrays.fill(tuple, 0, 10, (byte) ('0'+(i%10)));
				Arrays.fill(tuple, 10, 20, (byte) ('a'+(i%26)));
				Arrays.fill(tuple, 20, 30, (byte) ('A'+(i%26)));
				Arrays.fill(tuple, 30, 40, (byte) ('a'+(i%26)));
				dbtable.addTuple(tuple);
			}
		}
		SGBD.closeConnection();
		
		TableDescription in = new DefaultTableDescription("TABLE_ONE", char10, char10, char10, char10);
		TableDescription out = new DefaultTableDescription("RESULT_TABLE", char10, char10);
		RecordFilter filter = Factory.newRecordFilter(in, out);
		byte[] ten_a = new byte[10];
		Arrays.fill(ten_a, (byte) 'a'); 
		filter.addSelector(new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(1, ten_a)));
		filter.setProjectors(new int[]{1,2});
		
		SGBD.openConnection();
		
		byte[] ten_1 = new byte[10];
		Arrays.fill(ten_1, (byte) '1'); 
		
		try (PhysicalOperator table_index_0_equals_1 = Factory.newTableMapIndexedTraversalOperator("TABLE_ONE", 0, ten_1);
			 PhysicalOperator select_1_equals_a_project_1_2 = Factory.newFilterOperator(table_index_0_equals_1, filter)) {
		
			byte[] tuple;
			while ((tuple = select_1_equals_a_project_1_2.nextRecord()) != null) {
				System.out.println(Arrays.toString(tuple));
			}
		}
		SGBD.closeConnection();
		
	}
	
}
