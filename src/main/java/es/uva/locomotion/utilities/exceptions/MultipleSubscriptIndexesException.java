package es.uva.locomotion.utilities.exceptions;


/**
 * Thrown to indicate that the symbol is indexed with two or more different subscripts (not subscript values)
 * An example file that would raise this exception is: <br>
 * <pre>
 * COUNTRIES: USA, SPAIN, ITALY ~~|
 * ENERGY_SOURCE: COAL, SOLAR ~~|
 * var[COUNTRIES] = 3 ~~|
 * var[ENERGY_SOURCE] = 3 ~~|
 * </pre>
 */
public class MultipleSubscriptIndexesException extends RuntimeException {
}
