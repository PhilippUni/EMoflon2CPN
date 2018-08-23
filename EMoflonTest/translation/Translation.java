/**
 * 
 */
package translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import translation.chooser.ChooserException;
import translation.chooser.ChooserImpl;
import translation.generator.GeneratorImpl;
import translation.lexer.LexerImpl;
import translation.mapper.MapperImpl;
import translation.parser.ParserImpl;
import translation.parser.XmlNode;

/**
 * @author Timo Freitag
 *
 */
public class Translation {
	private Lexer lexer;
	private Parser parser;
	private Chooser chooser;
	private Mapper mapper;
	private Inserter inserter;
	private Generator generator;
	private Class<?> chosenClass;
	private URLClassLoader classLoader;
	private DirectoryHandler directoryHandler;
	private Integer port = null;
	private File chosenInstance;
	private XmlNode chosenClassNode = null;
	private XmlNode chosenMethodNode = null;
	
	private static Translation translation;
	private Translation() {}
	
	public static Translation getTranslation() {
		if(translation == null) translation = new Translation();
		return translation;
	}
	
	public String getProjectName() {
		return directoryHandler.getProject().getName();
	}
	
	public Lexer getLexer() {
		return lexer;
	}

	public Parser getParser() {
		return parser;
	}

	public Chooser getChooser() {
		return chooser;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public Inserter getInserter() {
		return inserter;
	}

	public Generator getGenerator() {
		return generator;
	}

	public Class<?> getChosenClass() {
		return chosenClass;
	}
	
	public DirectoryHandler getDirectoryHandler() {
		return directoryHandler;
	}
	
	public URLClassLoader getClassLoader() {
		return classLoader;
	}
	
	public void load(File directory) {
		directoryHandler = TranslationFactory.getDirectoryHandler(directory);
		directoryHandler.initialize();
		String input;
		try {
			input = IOHandler.read(directoryHandler.getEcore());
		} catch (IOException e) {
			throw new TranslationException(e);
		}
		lexer = TranslationFactory.getLexer(input);
		parser = TranslationFactory.getParser(lexer.getTokenList());
		parser.getXmlTree();
	}
	
	public void translate(Integer port) {
		if(chosenClassNode == null) 
			throw new NothingChosenException("No class chosen");
		
		if(chosenMethodNode == null)			// changed from chosenClassNode 
			throw new NothingChosenException("No method chosen");
		
		this.port = port;
		
		try {
			URL[] urls = new URL[1];
			System.out.println(chosenMethodNode.getProperty("name"));
			urls[0] = directoryHandler.getBin().toURI().toURL();
			classLoader = new URLClassLoader(urls);
			chosenClass = classLoader.loadClass(directoryHandler.getFullClassName(chooser.getClassName() + "Impl"));
			mapper = TranslationFactory.getMapper(chosenMethodNode, port, chosenClass, chooser.getMethodName());
			inserter = TranslationFactory.getInserter(mapper.getMappedCpnTree());
			generator = TranslationFactory.getGenerator(inserter.getTree());
			IOHandler.write(directoryHandler.getCpn(), generator.generateCode());
		} catch (IOException | ClassNotFoundException e) {
			throw new TranslationException(e);
		}
	}
	
	public List<String> getClasses() {
		chooser = TranslationFactory.getChooser(parser.getXmlTree());
		return chooser.getClasses();
	}
	
	public void chooseClass(String chosenClass) {
		chosenClassNode = chooser.chooseClass(chosenClass);
	}
	
	public List<String> getMethods() {
		return chooser.getMethods();
	}
	
	public void chooseMethod(String chosenMethod) {
		chosenMethodNode = chooser.chooseMethod(chosenMethod);
	}
	
	public File getChosenXmiFile() {
		return chosenInstance;
	}

	public void chooseInstance(File instance) {
		chosenInstance = instance;
	}

	public Integer getPort() {
		return port;
	}
}
