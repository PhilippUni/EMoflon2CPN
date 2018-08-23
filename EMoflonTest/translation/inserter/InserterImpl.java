package translation.inserter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import translation.IOHandler;
import translation.Inserter;
import translation.Lexer;
import translation.Parser;
import translation.TranslationException;
import translation.TranslationFactory;
import translation.chooser.ChooserImpl;
import translation.generator.GeneratorImpl;
import translation.lexer.LexerException;
import translation.lexer.LexerImpl;
import translation.mapper.MapperImpl;
import translation.mapper.XmlNodeFactory;
import translation.parser.ParserException;
import translation.parser.ParserImpl;
import translation.parser.XmlNode;

public class InserterImpl implements Inserter {
	XmlNode node;
	XmlNode tree;
	public InserterImpl(XmlNode node) {
		this.node = node;
	}
	
	public XmlNode getTree() {
		try {
			tree = insert();
		} catch (IOException e) {
			throw new InserterException(e);
		}
		return tree;
	}
	
	private XmlNode insert() throws IOException {
		String path = "src/resources/emptynet.cpn";
		String input = IOHandler.read(new File(path));
		
		Lexer lexer = TranslationFactory.getLexer(input);
		Parser parser = TranslationFactory.getParser(lexer.getTokenList());
		XmlNode tree = parser.getXmlTree();
		XmlNode cpnet = tree.getChildren().stream().filter(n -> filterChild(n, "cpnet")).findFirst().get();
		cpnet.getChildren().add(1, node);
		Map<String, String> instanceProperties = new HashMap<String, String>();
		instanceProperties.put("id", XmlNodeFactory.getFactory().getNextId());
		instanceProperties.put("page", node.getProperty("id"));
		List<XmlNode> instances = new ArrayList<XmlNode>();
		instances.add(new XmlNode("instance", null, null, instanceProperties));
		cpnet.getChildren().add(2, new XmlNode("instances", null, instances, null));
		return tree;
	}
	
	private boolean filterChild(XmlNode node, String identifier) {
		return node.getIdentifier().equals(identifier);
	}
}
