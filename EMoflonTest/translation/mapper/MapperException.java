/**
 * 
 */
package translation.mapper;

/**
 * Exception of the mapper
 * 
 * @author Timo Freitag
 *
 */
public class MapperException extends RuntimeException {
	/**
	 * Constructs a MapperException with a given message
	 * 
	 * @param msg message
	 */
	public MapperException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a MapperException with a given exception
	 * 
	 * @param e exception
	 */
	public MapperException(Exception e) {
		super(e);
	}
}
