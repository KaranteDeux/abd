package abd.reldb.ra;

/** A join operation of the relational algebra. 
 * This is a binary operation, and is parameterized by a {@link JoinCriterion}.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public class JoinOperation implements RAOperation {

	private RAOperation leftOp;
	private RAOperation rightOp;
	private JoinCriterion jcrit;
	
	public JoinOperation(RAOperation leftOp, RAOperation rightOp, JoinCriterion jcrit) {
		this.leftOp = leftOp;
		this.rightOp = rightOp;
		this.jcrit = jcrit;
	}
	
	@Override
	public String toString() {
		return String.format("%s JOIN[%s] %s", leftOp, jcrit, rightOp);
	}
	
}
