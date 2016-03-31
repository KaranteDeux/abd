package abd.ra.phys;

import java.util.ArrayList;

import abd.schemas.TableDescription;

public abstract class AbstractRecordFilter {

	protected final TableDescription inputDescription;
	protected final TableDescription outputDescription;
	
	protected final ArrayList<Selector> selectors = new ArrayList<>();
	protected final ArrayList<Integer> projectors = new ArrayList<>();
	
	public AbstractRecordFilter(TableDescription inputDescription, TableDescription outputDescription) {
		super();
		this.inputDescription = inputDescription;
		this.outputDescription = outputDescription;
	}

	public void addSelector(Selector selector) {
		selectors.add(selector);
	}
	
	public void setProjectors (int... columnRanks) {
		for (int cr : columnRanks)
			projectors.add(cr);
	}

}
