package abd.ra.phys;

import java.io.IOException;
import java.util.Arrays;

public class DefaultFilterOperator implements PhysicalOperator{

	PhysicalOperator physOpe;
	DefaultRecordFilter recordFilter;
	
	public DefaultFilterOperator(PhysicalOperator physOpe, DefaultRecordFilter recordFilter){
		this.physOpe = physOpe;
		this.recordFilter = recordFilter;
	}
	
	@Override
	public void close() throws IOException {
		
	}

	@Override
	public byte[] nextRecord() throws IOException {
		byte [] record = null;
		byte [] recordFiltered = null;

		do {
			record = physOpe.nextRecord();
			
			if(record != null)
				recordFiltered = recordFilter.filter(record);

		} while(record != null && recordFiltered == null);
		
		return recordFiltered;
	}


}
