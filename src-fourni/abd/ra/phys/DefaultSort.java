package abd.ra.phys;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import abd.DBTableImpl;
import abd.phys.Page;

/** Allows to sort a table, storing the result in a new table.
 * A maximal number of pages that can be used is provided at construction time.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 10 mars 2016
 */
public class DefaultSort {

	String tableName;
	List<String> filenames;

	String resultTableName;

	int columnRank;

	Page writterPage;

	int nbReaderPages;



	public DefaultSort (String tableName, String resultTableName, int columnRank, int maxNumberPages) {
		this.tableName = tableName;
		this.resultTableName = resultTableName;

		this.columnRank = columnRank;

		File dir = new File(tableName);
		File[] filesTab = dir.listFiles();

		for(File fileTab : filesTab){
			filenames.add(fileTab.getName());
		}

		nbReaderPages = maxNumberPages - 1;

	}

	/** Performs the sorting, storing the result in the result table. */
	public void sort () {
		for(String filename : filenames){
			try{
				FileInputStream in = new FileInputStream(tableName + "/" + filename);
				byte [] b = new byte[DBTableImpl.PAGE_SIZE];
				in.read(b);
			} catch(Exception e){
				e.printStackTrace();
			}
			//Page page = new DefaultPageSequentialAccess(ByteBuffer.wrap(b), getNbBytesPerRecord());
		}

	}


}
