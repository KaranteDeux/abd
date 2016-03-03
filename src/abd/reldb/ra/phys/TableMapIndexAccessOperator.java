package abd.reldb.ra.phys;

import java.io.IOException;
import java.nio.file.Path;

import abd.Factory;
import abd.reldb.DBTable;
import abd.reldb.TableDescription;

public class TableMapIndexAccessOperator implements PhysicalOperator {
	
	DBTable table;
	byte [] attributeValue;
	int columnRank;
	
	public TableMapIndexAccessOperator(Path dataFolder, TableDescription tableDescr, byte[] attributeValue, int indexedColumnRank) throws IOException {
		table = Factory.newDBTableWithIndex(dataFolder, tableDescr, indexedColumnRank);
		this.attributeValue = attributeValue; 
		
	}

	@Override
	public byte[] nextRecord() throws IOException {
		return table.getTupleBySelection(columnRank, attributeValue);
		
	}

	@Override
	public void close() throws IOException {		
	}
	
}