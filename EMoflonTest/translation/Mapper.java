package translation;

import java.util.Map;

import translation.mapper.MapperException;
import translation.mapper.methodConstructor.EmoflonMethod;
import translation.parser.XmlNode;

public interface Mapper {
	public XmlNode getMappedCpnTree();
	public Map<String, EmoflonMethod> getMethods();
}
