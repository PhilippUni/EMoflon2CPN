/**
 * 
 */
package translation.mapper.methodConstructor;

import translation.mapper.MapperException;

/**
 * @author Timo Freitag
 *
 */
public class MethodConstructorException extends MapperException {
	public MethodConstructorException(String msg) {
		super(msg);
	}
	
	public MethodConstructorException(Exception e) {
		super(e);
	}
}
