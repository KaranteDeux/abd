package abd.ra;

import abd.schemas.TableDescription;

/** An operation of the relational algebra used as a leaf.
 * This represents the operation consisting of traversing the contents of a database table. 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class TableOperation implements RAOperation {

	private TableDescription tableDescr;
	
	public TableOperation(TableDescription tableDescr) {
		this.tableDescr = tableDescr;
	}
	
	@Override
	public String toString() {
		return tableDescr.getName();
	}
	
}
