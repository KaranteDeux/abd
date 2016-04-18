package abd.ra.phys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import abd.Factory;
import abd.SGBD;
import abd.phys.DBTableForAdding;
import abd.ra.JoinCriterion;
import abd.schemas.AttributeType;

public class TestNestedLoopsJoin {
	
	
	@Test
	public void testJoinSmallResult() throws IOException {
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();

		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{}, new int[]{});

		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				dbtable.addTuple(tuple);
			}
		}
		
		SGBD.createTable("TABLE_TWO", new AttributeType[] {char120, char120}, new int[]{}, new int[]{});
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_TWO")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_TWO").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('0'+i));
				if (i % 6 == 1) {
					Arrays.fill(tuple, 0, 120, (byte) ('a'+i));   // some values are made equal to a value of the first table
				}
				dbtable.addTuple(tuple);
			}
		}
		
		SGBD.closeConnection();
		
		
		SGBD.openConnection();
		
		ArrayList<String> results = new ArrayList<>();

		
		JoinCriterion jcrit = new JoinCriterion(new int[]{1,0});
		try (DefaultNestedLoopsJoin join = new DefaultNestedLoopsJoin("TABLE_ONE", "TABLE_TWO", jcrit, 2)) { 

			//System.out.println(join.nextRecord()[0]);
			//System.out.println(join.nextRecord()[0]);
			//System.out.println(join.nextRecord()[0]);

			byte[] record;
			while ((record = join.nextRecord()) != null) {
				String r = "" + (char) record[0] + (char) record[record.length-1];
				results.add(r);
				System.out.println(r);
			}
		}
		
		assertEquals(4, results.size());
		assertTrue(results.contains("b1"));
		assertTrue(results.contains("h7"));
		assertTrue(results.contains("n="));
		assertTrue(results.contains("tC"));
		
		SGBD.closeConnection();	
		
	}

	
	
	@Test
	public void testJoinBigResult() throws IOException {
		
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();

		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{}, new int[]{});

		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				dbtable.addTuple(tuple);
			}
		}
		
		SGBD.createTable("TABLE_TWO", new AttributeType[] {char120, char120}, new int[]{}, new int[]{});
		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_TWO")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_TWO").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('0'+i));
				if (i % 6 != 1) {
					Arrays.fill(tuple, 0, 120, (byte) ('a'+i));   // some values are made equal to a value of the first table
				}
				dbtable.addTuple(tuple);
			}
		}
		
		SGBD.closeConnection();
		
		
		SGBD.openConnection();
		
		ArrayList<String> results = new ArrayList<>();

		
		JoinCriterion jcrit = new JoinCriterion(new int[]{1,0});
		try (DefaultNestedLoopsJoin join = new DefaultNestedLoopsJoin("TABLE_ONE", "TABLE_TWO", jcrit, 2)) { 


			//System.out.println(join.nextRecord()[0]);
			//System.out.println(join.nextRecord()[0]);
			//System.out.println(join.nextRecord()[0]);

			byte[] record;
			while ((record = join.nextRecord()) != null) {
				String r = "" + (char) record[0] + (char) record[record.length-1];
				results.add(r);
				System.out.println(r);
			}
		}
		
		System.out.println(results);
		
		assertEquals(20-4, results.size());
		assertFalse(results.contains("b1"));
		assertFalse(results.contains("h7"));
		assertFalse(results.contains("n="));
		assertFalse(results.contains("tC"));
		
		SGBD.closeConnection();	
		
		
		
	}

}
