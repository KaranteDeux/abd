package abd.ra.phys;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import abd.Factory;
import abd.SGBD;
import abd.schemas.DBTable;

public class TableSequentialAccessOperator implements PhysicalOperator{

	
	DBTable table;
	
	public TableSequentialAccessOperator(String tableName) throws IOException{
		Path dataFolder = Paths.get(SGBD.DATA_FOLDER + '/' + tableName);
		table = Factory.newDBTableWithoutIndex(dataFolder, SGBD.getRelation(tableName).getTableDescription());

	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}


	@Override
	public byte[] nextRecord() throws IOException {
		return table.nextRecord();
	}

}
