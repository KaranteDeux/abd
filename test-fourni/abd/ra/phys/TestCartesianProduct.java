package abd.ra.phys;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import abd.Factory;
import abd.SGBD;
import abd.phys.DBTableForAdding;
import abd.phys.Page;
import abd.schemas.AttributeType;

public class TestCartesianProduct {

	@Test
	public void testPagesCartProd() throws IOException {
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
				dbtable.addTuple(tuple);
			}
		}
	
		SGBD.closeConnection();

		SGBD.openConnection();
	
		Page p1 = Factory.newPage(Factory.getPagesManager().loadPage("TABLE_ONE", 0, false).getByteBuffer(), "TABLE_ONE");
		Page p2 = Factory.newPage(Factory.getPagesManager().loadPage("TABLE_TWO", 0, false).getByteBuffer(), "TABLE_TWO");
		
		ArrayList<String> result = new ArrayList<>();
		
		DefaultPagesCartesianProduct cprod = new DefaultPagesCartesianProduct(p1, p2, 480);
		byte[] record;
		while ((record = cprod.nextRecord()) != null) {
			result.add("" + (char) record[0] + (char) record[record.length-1]);
		}
		cprod.close();

		// System.out.println(result);
		
		assertEquals(p1.getNbRecords()*p2.getNbRecords(), result.size());
		Set<String> resultSet = new HashSet<>(result);
		assertEquals(result.size(), resultSet.size());
				
		SGBD.closeConnection();	
	}
	
	@Test
	public void testTablesCartProduct() throws IOException {
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
				dbtable.addTuple(tuple);
			}
		}
	
		SGBD.closeConnection();
		
		
		SGBD.openConnection();
		
		
		DefaultCartesianProduct cprod = new DefaultCartesianProduct("TABLE_ONE", "TABLE_TWO", 2);
		
		ArrayList<String> result = new ArrayList<>();
		
		byte[] record;
		while ((record = cprod.nextRecord()) != null) {
			String r = "" + (char) record[0] + (char) record[record.length-1];
			result.add(r);
			System.out.println(r);
		}
		cprod.close();

		//System.out.println(result);
		
		int sizeCartProd = 20 * 20;
		assertEquals(sizeCartProd, result.size());
		Set<String> resultSet = new HashSet<>(result);
		System.out.println(result.size());
		assertEquals(result.size(), resultSet.size());
				
		SGBD.closeConnection();	
		
	}
	
	
	
}
