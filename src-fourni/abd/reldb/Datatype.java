package abd.reldb;

/** A subset of the SQL standard datatypes.
 * 
 * @author Iovka Boneva
 * This document is licensed under a Creative Commons Attribution 3.0 License: http://creativecommons.org/licenses/by/3.0/
 * 15 janv. 2016
 */
public enum Datatype {
	
	/** String of characters with fixed length. */
	CHARACTER, 
	/** String of characters with variable length up to some maximal value. */
	VARCHAR, 
	/** True or false*/
	BOOLEAN,
	/** Integer number with fixed precision (maximal number of digits). */
	INTEGER,
	/** Decimal number with fixed precision (maximal total number of digits) and scale (maximal number of digits after the decimal). */  
	DECIMAL,
	/** Year, month, day, hour, minute, second. */
	TIMESTAMP;


}
