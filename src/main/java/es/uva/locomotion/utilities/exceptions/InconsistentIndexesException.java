package es.uva.locomotion.utilities.exceptions;


/**
 * Thrown to indicate that the number of indexes varies depending on the line.
 * An example file that would raise this exception is: <br>
 *     <pre>
 * var[SCENARIO_1, COAL] = 3 ~~|
 * var[SCENARIO_2] = 3 ~~|
 * </pre>
 */
public class InconsistentIndexesException extends RuntimeException {


}
