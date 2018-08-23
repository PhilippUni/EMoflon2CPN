package translation;

import java.io.File;
import java.util.List;

import translation.chooser.ChooserException;
import translation.parser.XmlNode;

public interface Chooser {
	public List<String> getClasses();
	
	public List<String> getMethods();

	public XmlNode chooseClass(String name);

	public XmlNode chooseMethod(String name);
	
	public XmlNode getMethodNode();
	
	public String getMethodName();
	
	public String getClassName();
	
	public Class<?> getChosenClass();
}
