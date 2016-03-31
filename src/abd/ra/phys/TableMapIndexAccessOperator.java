package abd.ra.phys;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import utils.Utils;
import abd.Factory;
import abd.schemas.DBTable;
import abd.schemas.TableDescription;

public class TableMapIndexAccessOperator implements PhysicalOperator {
	
	DBTable table;
	byte [] attributeValue;
	int columnRank;
	
	public TableMapIndexAccessOperator(Path dataFolder, TableDescription tableDescr, byte[] attributeValue, int indexedColumnRank) throws IOException {
		table = Factory.newDBTableWithIndex(dataFolder, tableDescr, indexedColumnRank);
		this.attributeValue = attributeValue; 
		this.columnRank = indexedColumnRank;
	}

	@Override
	public byte[] nextRecord() throws IOException {
		byte[] record;
		do {
			record = table.nextRecord();
		}while(record != null && !Arrays.equals(attributeValue, Utils.getColumnFromColumnRank(table.getTableDescription(), columnRank, record)));
		
		return record;
		
	}

	@Override
	public void close() throws IOException {		
	}
	
}