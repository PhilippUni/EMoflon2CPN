/**
 * 
 */
package simulation;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Comparator;

import translation.Chooser;
import translation.Translation;
import translation.TranslationException;
import translation.mapper.EmoflonAddressInterpreter;
import translation.mapper.methodConstructor.EmoflonMethod;
import translation.mapper.methodConstructor.MethodWrapper;
import translation.mapper.methodConstructor.ObjectWrapper;
import translation.parser.XmlNode;

/**
 * @author Timo Freitag
 *
 */
public class Simulation {
	private String projectName;
	private Object object;
	private Map<String, Object> objects = new HashMap<String, Object>();
	private Map<String, EmoflonMethod> methods;
	private XmiReader reader;
	private File xmiFile;
	
	public void initialize() {
		Translation translation = Translation.getTranslation();
		projectName = translation.getProjectName();
		reader = new XmiReader(translation.getClassLoader());
		reader.initialize(projectName);
		xmiFile = translation.getChosenXmiFile();
		object = reader.load(xmiFile);
		objects.put("this", object);
		methods = Translation.getTranslation().getMapper().getMethods();
	}
	
	public void close() {
		reader.save(object);
	}
	
	
	public boolean invoke(String name) throws SimulationException {
		try {
			EmoflonMethod emoflonMethod = methods.get(name);
			MethodWrapper blackMethod = emoflonMethod.getBlackMethod();
			MethodWrapper greenMethod = emoflonMethod.getGreenMethod();
			MethodWrapper redMethod = emoflonMethod.getRedMethod();
			
			if(!invokeColour(blackMethod)) return false;
			if(!invokeColour(redMethod)) return false;
			for(String objectToDelete : emoflonMethod.getObjectsToDelete()) {
				EcoreUtil.delete((EObject) objects.get(objectToDelete));
				objects.remove(objectToDelete);
			}
			if(!invokeColour(greenMethod)) return false;
			
			return true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new SimulationException("Invocation of method " + name + " crashed. " + e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	public boolean invokeColour(MethodWrapper method) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(method.getMethod() == null) return true;
		int i = 0;
		Object[] args = new Object[method.getInput().size()];
		for(ObjectWrapper arg : method.getInput()) {
			args[i++] = objects.get(arg.getName());
		}
		
		Object[] returnObjects = (Object[]) method.getMethod().invoke(null, args);
		if(returnObjects == null) return false;
		
		i = 0;
		for(String returnObject : method.getOutput()) {
			objects.put(returnObject, returnObjects[i++]);
		}
		return true;
	}
	
	
}
