package abd.reldb.ra;

/** A selection operation of the relational algebra. 
 * This is a unary operation and is parameterized by a {@link SelectionCriterion}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class SelectionOperation implements RAOperation {

	private RAOperation subOp;
	private SelectionCriterion scrit;
	
	public SelectionOperation (RAOperation subOperation, SelectionCriterion scrit) {
		subOp = subOperation;
		this.scrit = scrit;
	}
	
	@Override
	public String toString() {
		return String.format("SELECT[%s](%s)", scrit, subOp);
	}
	
}
