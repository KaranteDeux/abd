package abd.ra.phys;

import java.util.Arrays;
import java.util.Comparator;

import abd.ra.SelectionCriterion_ComparisonWithConstant;
import abd.schemas.TableDescription;

public class Selector_ComparisonWithConstant implements Selector {

	TableDescription tableDescription;
	SelectionCriterion_ComparisonWithConstant selectionComparison;

	public Selector_ComparisonWithConstant(TableDescription tableDescription, SelectionCriterion_ComparisonWithConstant selectionComparison){
		this.tableDescription = tableDescription;
		this.selectionComparison = selectionComparison;
	}

	@Override
	public boolean isSelected(byte[] record) {
		int columnRank = selectionComparison.getColumnRank();
		byte [] column = getColumnAtRank(columnRank, record);
		
		int ope = selectionComparison.getDirection();
		boolean isStrict = selectionComparison.isStrict();
		
		Comparator<byte []> comp = selectionComparison.getComparator();
		int resComp = comp.compare(column, selectionComparison.getConstant());
		
		if(ope > 0 && !isStrict)
			return resComp >= 0;
		else if(ope > 0 && isStrict)
			return resComp > 0;
		else if(ope < 0 && !isStrict)
			return resComp <= 0;
		else if(ope < 0 && isStrict)
			return resComp < 0;
		
		
		// Ne peut jamais être atteint, car dans la definition de SelectionCriterion_Comparison..,
		// ope ne peut être egal à 0 
		return false;
	}



	private byte[] getColumnAtRank(int columnRank, byte[] tuple){
		int cpt = 0, size;
		for(int i=0;i<columnRank;i++){
			cpt += this.tableDescription.getAttributeType(i).getLength();
		}

		size = this.tableDescription.getAttributeType(columnRank).getLength();

		return Arrays.copyOfRange(tuple, cpt, cpt+size);


	}

}
