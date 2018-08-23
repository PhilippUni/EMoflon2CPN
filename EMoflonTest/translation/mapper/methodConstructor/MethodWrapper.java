/**
 * 
 */
package translation.mapper.methodConstructor;

import java.lang.reflect.Method;
import java.util.SortedSet;

/**
 * @author Timo Freitag
 *
 */
public class MethodWrapper {
	private SortedSet<ObjectWrapper> input;
	private SortedSet<String> output;
	private Method method;

	public MethodWrapper(Method method, SortedSet<ObjectWrapper> input, SortedSet<String> output) {
		this.input = input;
		this.output = output;
		this.method = method;
	}

	public SortedSet<ObjectWrapper> getInput() {
		return input;
	}

	public SortedSet<String> getOutput() {
		return output;
	}

	public Method getMethod() {
		return method;
	}
	

}
