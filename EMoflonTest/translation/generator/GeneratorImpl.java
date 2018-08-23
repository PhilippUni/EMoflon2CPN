package translation.generator;

import translation.Generator;
import translation.parser.XmlNode;

public class GeneratorImpl implements Generator {
	private String code;
	private XmlNode tree;
	
	public GeneratorImpl(XmlNode tree) {
		this.tree = tree;
	}
	
	public String generateCode()  {
		if (code == null)
			code = startGenerate();
		return code;
	}
	
	private String startGenerate() {
		StringBuilder contentBuilder = new StringBuilder();
		StringBuilder tabBuilder = new StringBuilder();
		contentBuilder.append("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n");
		contentBuilder.append("<!DOCTYPE workspaceElements PUBLIC \"-//CPN//DTD CPNXML 1.0//EN\" \"http://www.daimi.au.dk/~cpntools/bin/DTD/5/cpn.dtd\">\n\n");
		xmlNodeIntoStringBuilder(tree, contentBuilder, tabBuilder, 0);
		return contentBuilder.toString();
	}
	
	private void xmlNodeIntoStringBuilder(XmlNode node, StringBuilder contentBuilder, StringBuilder tabBuilder, int tabCount) {
		String tabs = tabBuilder.toString();
		tabs = tabs.length() <= tabCount ? tabs : tabs.substring(0, tabCount);
		contentBuilder.append(tabs);
		contentBuilder.append('<');
		contentBuilder.append(node.getIdentifier());
		node.getProperties().forEach((identifer, content) 
				-> contentBuilder.append(' ').append(identifer)
				.append("=\"").append(content).append('"'));
	
		if(node.getChildren().isEmpty() && node.getContent() == null) {
			contentBuilder.append("/>");
		} else {
			contentBuilder.append('>');
			if(node.getContent() != null) {
				contentBuilder.append(node.getContent());
			}
			if (tabs.length() <= tabCount)
				tabBuilder.append("  ");
			node.getChildren().forEach(
					child -> xmlNodeIntoStringBuilder(child, contentBuilder.append("\n"), tabBuilder, tabCount + 2));

			if(node.getChildren().isEmpty()) {
				contentBuilder.append('<').append('/').append(node.getIdentifier()).append(">");
			} else {
				contentBuilder.append('\n');
				contentBuilder.append(tabs);
				contentBuilder.append('<').append('/').append(node.getIdentifier()).append(">");
			}
			
		}
	}
}
