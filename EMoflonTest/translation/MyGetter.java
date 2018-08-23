package translation;

import translation.mapper.MapperException;
import translation.parser.XmlNode;

public class MyGetter {

	public static String getProperty(XmlNode node, String property) throws MapperException {
		if(node == null) return null;
		if(!node.getProperties().containsKey(property)) return "nope";
		String value = node.getProperty(property);
		if(value == null) throw new MapperException("Mapper found no property " + property + " in node " + node.getIdentifier());
		return value;
	}
	/**public static String getChild(XmlNode node, String identifier) {
		if(node==null) return null;
		if(node.getChild(identifier)==null) return null;
		
		
		
		
		
		return value;
		
	}*/
}
