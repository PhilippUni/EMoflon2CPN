/**
 * 
 */
package translation.mapper.methodConstructor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Timo Freitag
 *
 */
public class EmoflonMethod {
	private MethodWrapper blackMethod;
	private MethodWrapper greenMethod;
	private MethodWrapper redMethod;
	private List<String> objectsToDelete;
	private String name;

	public EmoflonMethod(String name, MethodWrapper blackMethod, MethodWrapper greenMethod, MethodWrapper redMethod, List<String> objectsToDelete) {
		this.name = name;
		this.blackMethod = blackMethod;
		this.greenMethod = greenMethod;
		this.redMethod = redMethod;
		this.objectsToDelete = objectsToDelete;
	}
	
	public String getName() {
		return name;
	}

	public MethodWrapper getBlackMethod() {
		return blackMethod;
	}

	public MethodWrapper getGreenMethod() {
		return greenMethod;
	}

	public MethodWrapper getRedMethod() {
		return redMethod;
	}
	
	public List<String> getObjectsToDelete() {
		return objectsToDelete;
	}
}
