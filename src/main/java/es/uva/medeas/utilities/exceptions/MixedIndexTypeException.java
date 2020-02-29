package es.uva.medeas.utilities.exceptions;


/**
 * Thrown to indicate that the symbol is indexed with at least a value and a full subscript. <br>
 * Symbols can be indexed by either a full subscript, or by multiple subscript values, but not both at the same time. <br>
 * An example file that would raise this exception is: <br>
 * <pre>
 * COUNTRIES: USA, SPAIN, ITALY ~~|
 * ENERGY_SOURCE: COAL, SOLAR ~~|
 * var[COUNTRIES] = 3 ~~|
 * var[COAL] = 3 ~~|
 * </pre>
 */
public class MixedIndexTypeException extends RuntimeException{
}
