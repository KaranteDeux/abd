package abd.ra.phys;

import java.util.Arrays;

import utils.Utils;

import abd.ra.SelectionCriterion_EqualityToConstant;
import abd.schemas.TableDescription;

public class Selector_EqualityToConstant implements Selector{

	TableDescription tableDescription;
	SelectionCriterion_EqualityToConstant selectionEquality;
	
	public Selector_EqualityToConstant(TableDescription tableDescription, SelectionCriterion_EqualityToConstant selCrit){
		this.tableDescription = tableDescription;
		this.selectionEquality = selCrit;
	}
	
	@Override
	public boolean isSelected(byte[] record) {
		int columnRank = selectionEquality.getColumnRank();
		byte [] column = Utils.getColumnFromColumnRank(tableDescription, columnRank, record);
		
		return Arrays.equals(column, selectionEquality.getConstant());
	}
	
}
