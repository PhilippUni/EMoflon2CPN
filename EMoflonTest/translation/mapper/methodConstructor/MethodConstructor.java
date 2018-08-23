/**
 * 
 */
package translation.mapper.methodConstructor;

import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import java.util.List;

import simulation.Server;
import simulation.SimulationException;
import translation.DirectoryHandler;
import translation.MyGetter;
import translation.Translation;
import translation.TranslationException;
import translation.mapper.EmoflonAddressInterpreter;
import translation.mapper.MapperImpl;
import translation.parser.XmlNode;
import translation.mapper.methodConstructor.ObjectWrapper;

/**
 * @author Timo Freitag
 * @author Philipp Adolf
 *
 */
public class MethodConstructor {
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private URLClassLoader classLoader;
	private Class<?> chosenClass;
	private MethodNameConstructor methodNameConstructor;
	private String chosenMethod;
	private DirectoryHandler directoryHandler;
	
	
	public MethodConstructor(Class<?> chosenClass, String chosenMethod) {
		this.chosenClass = chosenClass;
		this.chosenMethod = chosenMethod;
		this.classLoader = Translation.getTranslation().getClassLoader();
		this.directoryHandler = Translation.getTranslation().getDirectoryHandler();
	}
	
	public void initialize() {
		methodNameConstructor = new MethodNameConstructor(Translation.getTranslation().getParser(),
				chosenClass.getSimpleName().replace("Impl", ""), chosenMethod);
		methodNameConstructor.initialize();
		
	}
	
	public EmoflonMethod constructMethod(XmlNode ownedActivityNode) {
		XmlNode forEachPattern =null;
		XmlNode storyPattern = ownedActivityNode.getChild("storyPattern");
		String isForEach = MyGetter.getProperty(ownedActivityNode, "forEach"); // danach testen ob es null ist.
		String activityName = ownedActivityNode.getProperty("name");
		String storyComment = storyPattern.getProperty("comment");
		
		
		if(!activityName.equals(storyComment))
			throw new MethodConstructorException("Methodname " + activityName + " isnt the same as the story pattern comment " + storyComment);				
		XmlNode[] objectVariables = storyPattern.getChildren().stream().filter(n -> n.getIdentifier().equals("objectVariable")).toArray(XmlNode[]::new);
		List<String> objectNames = Arrays.stream(objectVariables).map(n -> n.getProperty("name")).collect(Collectors.toList());
		XmlNode[] linkVariables = storyPattern.getChildren().stream().filter(n -> n.getIdentifier().equals("linkVariable")).toArray(XmlNode[]::new);
		
		Comparator<String> stringComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2){
				int i1 = objectNames.indexOf(o1);
				int i2 = objectNames.indexOf(o2);
				if(i1 < 0) throw new MethodConstructorException(o1 + "not found in ObjectList");
				if(i2 < 0) throw new MethodConstructorException(o2 + "not found in ObjectList");
				return Integer.compare(i1, i2);
			}
		};
		
		Comparator<ObjectWrapper> objectComparator = new Comparator<ObjectWrapper>() {
			@Override
			public int compare(ObjectWrapper o1, ObjectWrapper o2) {
				int i1 = objectNames.indexOf(o1.getName());
				int i2 = objectNames.indexOf(o2.getName());;
				if(i1 < 0) throw new MethodConstructorException(o1.getName() + "not found in ObjectList");
				if(i2 < 0) throw new MethodConstructorException(o2.getName() + "not found in ObjectList");
				return Integer.compare(i1, i2);
			}
		};
		
		SortedSet<ObjectWrapper> blackInput = new TreeSet<ObjectWrapper>(objectComparator);
		SortedSet<String> blackOutput = new TreeSet<String>(stringComparator);
		SortedSet<ObjectWrapper> greenInput = new TreeSet<ObjectWrapper>(objectComparator);
		SortedSet<String> greenOutput = new TreeSet<String>(stringComparator);
		SortedSet<ObjectWrapper> redInput = new TreeSet<ObjectWrapper>(objectComparator);
		SortedSet<String> redOutput = new TreeSet<String>(stringComparator);
		List<String> objectsToDelete = new ArrayList<String>();
		if(isForEach.contentEquals("true")) {
			System.out.println("muss was machen");
			
		}
		for(XmlNode objectVariable : objectVariables) {
			String bindingOperator = objectVariable.getProperty("bindingOperator");
			List<XmlNode> assignments = new ArrayList<XmlNode>();
			
			int numberOfChildren = objectVariable.getChildren().size();
			int i=0;
			while(i<numberOfChildren) {
				XmlNode child =objectVariable.getChildren().get(i);
				if(child.getIdentifier().equals("attributeAssignment")) {
					assignments.add(child);
					String assignedAttributName=MyGetter.getProperty(child,"attribute");
					XmlNode valueToAssign=child.getChild("valueExpression");
					String attributeToAssign =null;
					String xsiType =MyGetter.getProperty(valueToAssign, "xsi:type");
					if(xsiType.equals("patternExpressions:AttributeValueExpression")) {
						attributeToAssign = MyGetter.getProperty(valueToAssign,"attribute");	//do the necessary
					}
					else if(xsiType.equals("ParamentalValueExpression")){
						//do the necessary
					}
					
					
					//objectVariable.;
					
				}
				i++;
			}

			String name = objectVariable.getProperty("name");
			List<String> address = EmoflonAddressInterpreter.addressToList(objectVariable.getProperty("type"));
			Class<?> type;
			try {
				type = classLoader.loadClass(directoryHandler.getFullClassName(address.get(address.size() - 1)));
			} catch (ClassNotFoundException e) {
				throw new MethodConstructorException(e);
			}
			classes.put(address.get(address.size() - 1), type);
			String bindingState = objectVariable.getProperty("bindingState");
			String bindingSemantics = objectVariable.getProperty("bindingSemantics");
			if(bindingSemantics != null && bindingSemantics.equals("NEGATIVE")) continue;
			//TODO try to programm this more nicely
			if(bindingOperator != null) {
				if(bindingOperator.equals("CREATE")) {
					greenOutput.add(name);
				}
				else if(bindingOperator.equals("DESTROY")) {
					redInput.add(new ObjectWrapper(type, name));
					redOutput.add(name);
					objectsToDelete.add(name);
					if(bindingState != null && bindingState.equals("BOUND")) {
						blackInput.add(new ObjectWrapper(type, name));
					}
					blackOutput.add(name);
				}
			}
			else if(isForEach.equals("true")&&bindingState==null){
				System.out.println("jetzt teste ich im for");
				//blackOutput.add(name);
				
			}
			else {
				if(bindingState != null && bindingState.equals("BOUND")) {
					blackInput.add(new ObjectWrapper(type, name));
				}
				blackOutput.add(name);
			}
		}
		
		for(XmlNode linkVariable : linkVariables) {
			String bindingOperator = linkVariable.getProperty("bindingOperator");
			if(bindingOperator != null) {
				if(bindingOperator.equals("CREATE")) {
					addGreenLinkVariable(linkVariable, "source", objectVariables, greenInput, greenOutput);
					addGreenLinkVariable(linkVariable, "target", objectVariables, greenInput, greenOutput);
				}
				else if(bindingOperator.equals("DESTROY")) {
					addRedLinkVariable(linkVariable, "source", objectVariables, redInput, redOutput);
					addRedLinkVariable(linkVariable, "target", objectVariables, redInput, redOutput);
				}
			}
		}
		
		String methodName = storyPattern.getProperty("comment");
		Method blackMethod = null;
		Method greenMethod = null;
		Method redMethod = null;
		
		blackMethod = findMethod(chosenClass, methodNameConstructor.getMethodNameWithWildcard(methodName, "black", blackInput, blackOutput));
		//blackMethod = chosenClass.getMethod(methodNameConstructor.getMethodName(methodName, "black", blackInput, blackOutput), getTypes(blackInput));
		if(greenOutput.size() > 0)
			greenMethod = findMethod(chosenClass, methodNameConstructor.getMethodNameWithWildcard(methodName, "green",greenInput, greenOutput));
			//greenMethod = chosenClass.getMethod(methodNameConstructor.getMethodName(methodName, "green",greenInput, greenOutput), getTypes(greenInput));
		if(redOutput.size() > 0)
			redMethod = findMethod(chosenClass, methodNameConstructor.getMethodNameWithWildcard(methodName, "red", redInput, redOutput));
			//redMethod = chosenClass.getMethod(methodNameConstructor.getMethodName(methodName, "red", redInput, redOutput), getTypes(redInput));
		
		MethodWrapper blackMethodWrapper = new MethodWrapper(blackMethod, blackInput, blackOutput);
		MethodWrapper greenMethodWrapper = new MethodWrapper(greenMethod, greenInput, greenOutput);
		MethodWrapper redMethodWrapper = new MethodWrapper(redMethod, redInput, redOutput);
		return new EmoflonMethod(methodName, blackMethodWrapper, greenMethodWrapper, redMethodWrapper, objectsToDelete);
	}
	
	private void addGreenLinkVariable(XmlNode linkVariable, String sourceOrTarget, XmlNode[] objectVariables,
			SortedSet<ObjectWrapper> greenInput, SortedSet<String> greenOutput) {
		int index = EmoflonAddressInterpreter.addressToNumber(linkVariable.getProperty(sourceOrTarget));
		XmlNode objectVariable = objectVariables[index];
		String name = objectVariable.getProperty("name");
		List<String> address = EmoflonAddressInterpreter.addressToList(objectVariable.getProperty("type"));
		Class<?> type = classes.get(address.get(0));
		String binding = objectVariable.getProperty("bindingOperator");
		if(binding == null || !binding.equals("CREATE")) {
			greenInput.add(new ObjectWrapper(type, name));
		}
		greenOutput.add(name);
	}
	
	private void addRedLinkVariable(XmlNode linkVariable, String sourceOrTarget, XmlNode[] objectVariables,
			SortedSet<ObjectWrapper> redInput, SortedSet<String> redOutput) {
		int index = EmoflonAddressInterpreter.addressToNumber(linkVariable.getProperty(sourceOrTarget));
		XmlNode objectVariable = objectVariables[index];
		String name = objectVariable.getProperty("name");
		List<String> address = EmoflonAddressInterpreter.addressToList(objectVariable.getProperty("type"));
		Class<?> type = classes.get(address.get(0));
		redInput.add(new ObjectWrapper(type, name));
		redOutput.add(name);
	}
	
	private Method findMethod(Class<?> c, String name) {
	    String regex = ("\\Q" + name + "\\E").replace("*", "\\E.*\\Q");
		for(Method m : c.getMethods()) {
			if(m.getName().matches(regex))
				return m;
		}
		return null;
	}
	
	private Class<?>[] getTypes(SortedSet<ObjectWrapper> objectWrappers) {
		return objectWrappers.stream().map(o -> o.getType()).toArray(Class[]::new);
	}
}
