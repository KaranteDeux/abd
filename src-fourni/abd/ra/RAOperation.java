package abd.ra;


/** The top-level interface representing an operation of the relational algebra (select, project, join, table).
 * The different classes implementing this interface allow to encode an expression of the relational algebra. 
 * An expression of the relational algebra has a tree structure which nodes are {@link RAOperation}s, and every node has
 * as many children as the arity of the corresponding operation (i.e. a join node has two children, a projection or selection node has
 * one child, and a table node is a leaf in the tree).
 * 
 * The type hierarchy of {@link RAOperation} and all related classes (e.g. join and selection criterions) are used to describe the 
 * structure of an expression of the relational algebra, but <b>do not allow</b> to evaluate the expression. 
 * For evaluating the expressions, one has to use a {@link PhysicalOperator}. 
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 19 févr. 2016
 */
public interface RAOperation {}
