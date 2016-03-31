package abd.ra.phys;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import abd.Factory;
import abd.ra.SelectionCriterion_ComparisonWithConstant;
import abd.ra.SelectionCriterion_EqualityToConstant;
import abd.ra.SelectionCriterion_EqualityTwoAttributes;
import abd.schemas.AttributeType;
import abd.schemas.DefaultTableDescription;
import abd.schemas.TableDescription;

public class TestRecordFilter {

	@Test
	public void testSelectorEqualityToConstant() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		
		Selector col0_is_aa = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(0, new byte[]{'a','a'}));
		Selector col2_is_bb = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(2, new byte[]{'b','b'}));
		
		byte[] record0in = new byte[]{'a','a','x','x','b','b','y','y'};
		byte[] record1in = new byte[]{'a','a','x','x','c','c','y','y'};
		
		assertTrue(col0_is_aa.isSelected(record0in));
		assertTrue(col2_is_bb.isSelected(record0in));
		
		assertTrue(col0_is_aa.isSelected(record1in));
		assertFalse(col2_is_bb.isSelected(record1in));
	}
	
	@Test
	public void testFilterEqualityConstant() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		TableDescription out = new DefaultTableDescription("OTHERTABLE", char2, char2);
		
		Selector col0_is_aa = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(0, new byte[]{'a','a'}));
		Selector col2_is_bb = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(2, new byte[]{'b','b'}));
		
		DefaultRecordFilter filter = new DefaultRecordFilter(in, out);
		filter.addSelector(col0_is_aa);
		filter.addSelector(col2_is_bb);
		filter.setProjectors(new int[]{0,3});
		
		byte[] record0in = new byte[]{'a','a','x','x','b','b','y','y'};
		byte[] record0expout = new byte[]{'a','a','y','y'};
				
		byte[] record1in = new byte[]{'a','a','x','x','c','c','y','y'};
		
		byte[] record0out = filter.filter(record0in);
		byte[] record1out = filter.filter(record1in);
		
		assertArrayEquals(record0expout, record0out);
		assertNull(record1out);
	}
	
	@Test
	public void testFilterEqualityTwoAttributes() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		TableDescription out = new DefaultTableDescription("OTHERTABLE", char2, char2);
		
		Selector col0_equals_col2 = new Selector_EqualityTwoAttributes(in, new SelectionCriterion_EqualityTwoAttributes(0,2));
		Selector col1_is_xx = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(1, new byte[]{'x','x'}));
		
		RecordFilter filter = Factory.newRecordFilter(in, out);
		filter.addSelector(col0_equals_col2);
		filter.addSelector(col1_is_xx);
		filter.setProjectors(new int[]{0,3});
		
		byte[] record0in = new byte[]{'a','a','x','x','a','a','y','y'};
		byte[] record0expout = new byte[]{'a','a','y','y'};
				
		byte[] record1in = new byte[]{'a','a','x','x','c','c','y','y'};
		
		byte[] record0out = filter.filter(record0in);
		byte[] record1out = filter.filter(record1in);
		
		assertArrayEquals(record0expout, record0out);
		assertNull(record1out);
	}
	
	@Test
	public void testFilterComparisonAttributeConstant() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		TableDescription out = new DefaultTableDescription("OTHERTABLE", char2, char2);
		
		Comparator<byte[]> comp = new DefaultByteArrayComparator(); 
		SelectionCriterion_ComparisonWithConstant selCrit = 
				new SelectionCriterion_ComparisonWithConstant(0, new byte[]{'a','b'}, comp, -1, true);
		Selector col0_less_than_ab = new Selector_ComparisonWithConstant(in, selCrit);
		
		DefaultRecordFilter filter = new DefaultRecordFilter(in, out);
		filter.addSelector(col0_less_than_ab);
		filter.setProjectors(new int[]{0,3});
		
		byte[] record0in = new byte[]{'a','a','x','x','a','a','y','y'};
		byte[] record0expout = new byte[]{'a','a','y','y'};
				
		byte[] record1in = new byte[]{'a','b','x','x','c','c','y','y'};
		byte[] record2in = new byte[]{'b','b','x','x','c','c','y','y'};
		
		byte[] record0out = filter.filter(record0in);
		byte[] record1out = filter.filter(record1in);
		byte[] record2out = filter.filter(record2in);
		
		assertArrayEquals(record0expout, record0out);
		assertNull(record1out);
		assertNull(record2out);
	}
	
	
	@Test(expected=Exception.class)
	public void testFilterWithError1() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		TableDescription out = new DefaultTableDescription("OTHERTABLE", char2, char2);
		
		Selector col0_is_aa = new Selector_EqualityToConstant(in, new SelectionCriterion_EqualityToConstant(4, new byte[]{'a','a'}));
		
		DefaultRecordFilter filter = new DefaultRecordFilter(in, out);
		filter.addSelector(col0_is_aa);
		
		byte[] record0in = new byte[]{'a','a','x','x','b','b','y','y'};
		filter.filter(record0in); // the selector is on an unexisting attribute
	}
	
	@Test(expected=Exception.class)
	public void testWithError2() {
		AttributeType char2 = AttributeType.newCharacter(2); 
		TableDescription in = new DefaultTableDescription("SOMETABLE", char2, char2, char2, char2);
		TableDescription out = new DefaultTableDescription("OTHERTABLE", char2, char2);
			
		DefaultRecordFilter filter = new DefaultRecordFilter(in, out);
		filter.setProjectors(new int[]{4}); // projection on non existing attribute
		
		byte[] record0in = new byte[]{'a','a','x','x','b','b','y','y'};

		filter.filter(record0in);
	}
	
}
