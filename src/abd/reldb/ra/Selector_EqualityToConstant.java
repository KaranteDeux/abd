package abd.reldb.ra;

import java.util.Arrays;

import abd.reldb.TableDescription;
import abd.reldb.ra.phys.Selector;

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
		byte [] column = getColumnAtRank(columnRank, record);
		
		return Arrays.equals(column, selectionEquality.getConstant());
	}
	
	
	private byte[] getColumnAtRank(int columnRank, byte[] tuple){
		int cpt = 0, size;
		for(int i=0;i<columnRank;i++){
			cpt += tableDescription.getAttributeType(i).getLength();
		}
		
		size = tableDescription.getAttributeType(columnRank).getLength();
		
		return Arrays.copyOfRange(tuple, cpt, cpt+size);
		
		
	}
	
}
