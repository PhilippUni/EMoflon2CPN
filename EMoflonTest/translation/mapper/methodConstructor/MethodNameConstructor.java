/**
 * 
 */
package translation.mapper.methodConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import jdk.internal.dynalink.linker.MethodHandleTransformer;
import translation.Parser;
import translation.TranslationException;
import translation.chooser.ChooserException;
import translation.parser.XmlNode;

/**
 * @author Timo Freitag
 *
 */
public class MethodNameConstructor {
	private Parser parser;
	private int method;
	private int storyPattern;
	private String chosenClass;
	private String chosenMethod;
	private List<String> methods = new ArrayList<String>();
	private List<String> storyPatterns = new ArrayList<String>();
	private List<XmlNode> xmlMethods = new ArrayList<XmlNode>();
	
	public MethodNameConstructor(Parser parser, String chosenClass, String chosenMethod) {
		this.parser = parser;
		this.chosenClass = chosenClass;
		this.chosenMethod = chosenMethod;
	}
	
	public void initialize() {
		XmlNode methodNode = findMethodNumber();
		if(methodNode == null) throw new MethodNameConstructorException("MethodNameConstructor did not fount method " + chosenMethod + " in class" + chosenClass + ".");
		addStoryPatterns(methodNode);
		
	}
	
	private void addStoryPatterns(XmlNode methodNode) {
		XmlNode activity = null;
		String activityName = "//" + chosenClass.replace("Impl", "") + "/" + chosenMethod;
		for(XmlNode a : methodNode.getChildren()) {
			String owningOperation = a.getProperty("owningOperation");
			if(owningOperation != null && owningOperation.equals(activityName)) {
				activity = a;
				break;
			}
		}
		//assert activity != null : "Activity name is wrong: " + activityName;
		for(XmlNode activityNode : activity.getChildren()) {
			if(activityNode.getIdentifier().equals("ownedActivityNode")) {
				storyPatterns.add(activityNode.getProperty("name"));
			}
		}
	}
	
	private XmlNode findMethodNumber() {
		XmlNode node = parser.getXmlTree();
		if(!node.getIdentifier().equals("ecore:EPackage")) throw new MethodNameConstructorException("Current node isnt a ecore:Package. It is a " + node.getIdentifier());
		for(XmlNode eClassifier : node.getChildren()) {
			if(eClassifier.getIdentifier().equals("eClassifiers") && eClassifier.getProperty("name").equals(chosenClass.replace("Impl", ""))) {
				List<XmlNode> list = eClassifier.getChildren();
				for(int i = 0; i < list.size(); i++) {
					XmlNode methodNode = list.get(i);
					if(methodNode.getProperty("name").equals(chosenMethod)) {
						method = i;
						return methodNode;
					}
				}
			}
			
		}
		return null;
	}
	
	public String getMethodName(String name, String colour, SortedSet<ObjectWrapper> input, SortedSet<String> output) {
		return "pattern_" + chosenClass + "_" + method + "_" + storyPatterns.indexOf(name) + "_" + name + "_" + colour + constructBindings(input, output);
	}
	
	public String getMethodNameWithWildcard(String name, String colour, SortedSet<ObjectWrapper> input, SortedSet<String> output) {
		return "pattern_" + chosenClass + "_" + method + "_" + "*" +  "_" + name + "_" + colour + constructBindings(input, output);
	}
	private String constructBindings(SortedSet<ObjectWrapper> input, SortedSet<String> output) {
		List<String> inputStrings = input.stream().map(ow -> ow.getName()).collect(Collectors.toList());
		StringBuilder builder = new StringBuilder();
		for(String out : output) {
			builder.append(inputStrings.contains(out) ? 'B' : 'F');
		}
		return builder.toString();
	}
}
