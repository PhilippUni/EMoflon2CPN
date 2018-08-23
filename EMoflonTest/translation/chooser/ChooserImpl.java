package translation.chooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import translation.Chooser;
import translation.parser.XmlNode;

public class ChooserImpl implements Chooser {
	private XmlNode classesNode;
	private XmlNode methodsNode;
	private String chosenClassName;
	private String chosenMethodName;
	private String chosenXmiFileName;
	private XmlNode methodNode;
	private Class<?> chosenClass;
	private File[] xmlFiles;
	
	public ChooserImpl(XmlNode classesNode) {
		this.classesNode = classesNode;
	}
	
	public List<String> getClasses() {
		if(!classesNode.getIdentifier().equals("ecore:EPackage")) throw new ChooserException("Current node isn't an ecore:Package. It is a " + classesNode.getIdentifier());
		return classesNode.getChildren().stream().map(n -> n.getProperty("name"))
												 .collect(Collectors.toList());
	}
	
	public List<String> getMethods() {
		if(!methodsNode.getIdentifier().equals("eClassifiers")) throw new ChooserException("Current node isn't an eClassifiers. It is a " + methodsNode.getIdentifier());
		return methodsNode.getChildren().stream().filter(n -> n.getIdentifier().equals("eOperations"))
												 .map(n -> n.getProperty("name"))
												 .collect(Collectors.toList());
	
	}
	
	public XmlNode chooseClass(String name) {
		chosenClassName = name;
		if(!classesNode.getIdentifier().equals("ecore:EPackage")) throw new ChooserException("Current node isn't an ecore:Package. It is a " + classesNode.getIdentifier());
		return methodsNode = classesNode.getChildren().stream().filter(n -> n.getProperty("name").equals(chosenClassName)).findFirst().get();
	}
	
	public XmlNode chooseMethod(String name) {
		chosenMethodName = name;
		if(!methodsNode.getIdentifier().equals("eClassifiers")) throw new ChooserException("Current node isn't an eClassifiers. It is a " + methodsNode.getIdentifier());
		return methodsNode.getChildren().stream().filter(n -> n.getProperty("name").equals(chosenMethodName)).findFirst().get();
	}
	
	public XmlNode getMethodNode() {
		return this.methodNode;
	}
	
	public String getMethodName() {
		return this.chosenMethodName;
	}
	
	public String getClassName() {
		return this.chosenClassName;
	}
	
	public Class<?> getChosenClass() {
		return this.chosenClass;
	}
}
