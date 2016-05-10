package abd.ra.phys;

import java.util.Arrays;

import utils.Utils;
import abd.ra.SelectionCriterion_EqualityTwoAttributes;
import abd.schemas.TableDescription;

public class Selector_EqualityTwoAttributes implements Selector{

	TableDescription tableDescription;
	SelectionCriterion_EqualityTwoAttributes selectionCriterion;
	
	public Selector_EqualityTwoAttributes(TableDescription tableDescription, SelectionCriterion_EqualityTwoAttributes selectionCriterion){
		this.tableDescription = tableDescription;
		this.selectionCriterion = selectionCriterion;
		
	}
	
	@Override
	public boolean isSelected(byte[] record) {
		int columnRank1 = selectionCriterion.getColumnRank1();
		int columnRank2 = selectionCriterion.getColumnRank2();
		
		byte [] column1 = Utils.getColumnFromColumnRank(tableDescription, columnRank1, record);
		byte [] column2 = Utils.getColumnFromColumnRank(tableDescription, columnRank2, record);
		
		
		return Arrays.equals(column1, column2);
	}


}
