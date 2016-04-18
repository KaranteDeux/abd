package abd.ra.phys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import abd.Factory;
import abd.SGBD;
import abd.phys.DBTableForAdding;
import abd.ra.SelectionCriterion_EqualityTwoAttributes;
import abd.schemas.AttributeType;
import abd.schemas.TableDescription;

public class TestFilterOperator {

	@Test
	public void testFilterOperator() throws IOException {
		
		SGBD.destroySGBD();
		SGBD.createNewSGBD();
		SGBD.openConnection();

		AttributeType char120 = AttributeType.newCharacter(120);
		SGBD.createTable("TABLE_ONE", new AttributeType[] {char120, char120}, new int[]{}, new int[]{});

		try (DBTableForAdding dbtable = Factory.newDBTableForAdding("TABLE_ONE")) {
			byte[] tuple = new byte[SGBD.getRelation("TABLE_ONE").getTableDescription().getTupleLength()];
			for (int i = 0; i < 20; i++) {
				Arrays.fill(tuple, (byte) ('a'+i));
				if (i%5 != 1) {
					Arrays.fill(tuple, 120, 240, (byte) ('0'+i));
				} 
				dbtable.addTuple(tuple);
				
			}
		}
		
		SGBD.closeConnection();
		
		
		SGBD.openConnection();
		
		TableDescription descr = SGBD.getRelation("TABLE_ONE").getTableDescription();
		SelectionCriterion_EqualityTwoAttributes selCrit = new SelectionCriterion_EqualityTwoAttributes(0, 1);
		Selector sel = new Selector_EqualityTwoAttributes(descr, selCrit);
		DefaultRecordFilter filter = new DefaultRecordFilter(descr, descr);
		filter.addSelector(sel);
		filter.setProjectors(new int[]{0,1});
		
		PhysicalOperator seqTraversal = new TableSequentialAccessOperator("TABLE_ONE");
		try (DefaultFilterOperator filterOp = new DefaultFilterOperator(seqTraversal, filter)) {

			ArrayList<String> results = new ArrayList<>();
			byte[] record;
			while ((record = filterOp.nextRecord()) != null) {
				String r = "" + (char) record[0] + (char) record[record.length-1];
				System.out.println(r);
				results.add(r);
			}

			assertEquals(4, results.size());
			assertTrue(results.contains("bb"));
			assertTrue(results.contains("gg"));
			assertTrue(results.contains("ll"));
			assertTrue(results.contains("qq"));

		}
		SGBD.closeConnection();
	}
	
}
