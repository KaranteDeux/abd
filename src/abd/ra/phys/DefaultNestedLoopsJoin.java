package abd.ra.phys;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

import utils.Utils;
import abd.DBTableImpl;
import abd.SGBD;
import abd.ra.JoinCriterion;
import abd.schemas.DefaultTableDescription;
import abd.schemas.TableDescription;

/** Performs a join operation on two tables, using the nested loops algorithm.
 * A maximal number of pages that can be used is provided at construction time.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 10 mars 2016
 */
public class DefaultNestedLoopsJoin implements PhysicalOperator {

	DefaultCartesianProduct defaultCartesianProduct;
	String nameJoin;

	TableDescription joinDesc;

	DBTableImpl newTable;
	int columnTable1;
	int columnTable2;

	public DefaultNestedLoopsJoin (String tableName1, String tableName2, JoinCriterion jcrit, int maxNumberPages) throws IOException {

		nameJoin = tableName1 + "_JOIN_" + tableName2;

		defaultCartesianProduct = new DefaultCartesianProduct(tableName1, tableName2, maxNumberPages);


		TableDescription tableDescription1 = SGBD.getRelation(tableName1).getTableDescription();
		TableDescription tableDescription2 = SGBD.getRelation(tableName2).getTableDescription();


		Iterator<int[]> it = jcrit.iterator();
		int[] joinCrit = it.next();
		
		columnTable1 = joinCrit[0];

		columnTable2 = tableDescription1.getArity() + joinCrit[1];
		
		joinDesc = DefaultTableDescription.getDescriptionForJoin(nameJoin, tableDescription1, tableDescription2);

		Path pathJoin = Paths.get(SGBD.DATA_FOLDER + "/" + nameJoin);
		File dir = new File(pathJoin.toString());
		dir.mkdir();



		//System.out.println("path join :" + pathJoin + ", join Desc : " + joinDesc.toString());

		newTable = new DBTableImpl(pathJoin, joinDesc);


	}

	/** Computes the join, storing the result in the result table. 
	 * @throws IOException */
	public void join() throws IOException {
		byte[] record = null;
		do {
			record = nextRecord();

			newTable.addTuple(record);
		} while(record != null);
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public byte[] nextRecord() throws IOException {

		byte [] record = null;
		byte [] columnAt1 = null;
		byte [] columnAt2 = null;

		do {
			record = defaultCartesianProduct.nextRecord();
			if(record != null){
				columnAt1 = Utils.getColumnFromColumnRank(joinDesc, columnTable1, record);
				columnAt2 = Utils.getColumnFromColumnRank(joinDesc, columnTable2, record);
			}

		} while(record != null && !Arrays.equals(columnAt1, columnAt2));
		
		return record;

	}

}
