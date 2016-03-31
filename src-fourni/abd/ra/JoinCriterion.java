package abd.ra;

import java.util.ArrayList;
import java.util.Iterator;

/** Used as a parameter for a {@link JoinOperation}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class JoinCriterion implements Iterable<int[]>{

	private ArrayList<int[]> columnRanksCouples;
	
	public JoinCriterion(int[]... columnRanksCouples) {
		this.columnRanksCouples = new ArrayList<>();
		for (int i = 0; i < columnRanksCouples.length; i++) {
			this.columnRanksCouples.add(columnRanksCouples[i]);
		}
	}
	
	@Override
	public Iterator<int[]> iterator() {
		return columnRanksCouples.iterator();
	}

	@Override
	public String toString() {
		if (columnRanksCouples.isEmpty()) return "";
		StringBuilder s = new StringBuilder();
		int[] couple = columnRanksCouples.get(0);
		s.append(String.format("%d=%d", couple[0], couple[1]));
		for (int i = 1; i < columnRanksCouples.size(); i++) {
			couple = columnRanksCouples.get(i);
			s.append(String.format(",%d=%d", couple[0], couple[1]));
		}
		return s.toString();
	}
	
}
