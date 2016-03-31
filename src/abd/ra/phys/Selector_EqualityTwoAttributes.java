package abd.ra.phys;

import java.util.Arrays;

import abd.ra.SelectionCriterion_EqualityTwoAttributes;
import abd.schemas.TableDescription;

public class Selector_EqualityTwoAttributes implements Selector {

	TableDescription tableDecription;
	SelectionCriterion_EqualityTwoAttributes  selectionEquality;
	
	public Selector_EqualityTwoAttributes(TableDescription tableDescription, SelectionCriterion_EqualityTwoAttributes selCrit){
		this.tableDecription = tableDescription;
		this.selectionEquality = selCrit;
	}
	
	@Override
	public boolean isSelected(byte[] record) {
		int columnRank1 = selectionEquality.getColumnRank1();
		byte [] column1 = getColumnAtRank(columnRank1, record);
		
		int columnRank2 = selectionEquality.getColumnRank2();
		byte [] column2 = getColumnAtRank(columnRank2, record);
		
		return Arrays.equals(column1, column2);
	}
	
	
	private byte[] getColumnAtRank(int columnRank, byte[] tuple){
		int cpt = 0, size;
		for(int i=0;i<columnRank;i++){
			cpt += tableDecription.getAttributeType(i).getLength();
		}
		
		size = tableDecription.getAttributeType(columnRank).getLength();
		
		return Arrays.copyOfRange(tuple, cpt, cpt+size);
		
		
	}

}
