package translation.parser;

import java.util.List;

import translation.Parser;
import translation.TranslationException;
import translation.lexer.Token;
import translation.lexer.TokenType;

/**
 * This parser does a syntactical analysis of a given list of Tokens.
 * It is able to return a XmlNode tree.
 * 
 * @author Timo Freitag
 *
 */
public class ParserImpl implements Parser {

	private List<Token> listOfTokens;
	private int tokenListIndex = -1;
	private Token currentToken;
	
	private XmlNode tree;

	/**
	 * Constructs a parser which is able to handle xml-code
	 * 
	 * @param listOfTokens List of Tokens which the parser should parse.
	 */
	public ParserImpl(List<Token> listOfTokens) {
		this.listOfTokens = listOfTokens;
	}
	
	@Override
	public XmlNode getXmlTree() {
		if(tree == null)
			tree = startParsing();
		
		return tree;
	}
	
	/**
	 * Starts the parsing procedure.
	 * 
	 * @return the root of the XmlNode tree
	 */
	private XmlNode startParsing() {
		return handleNode();
	}
	
	/**
	 * Handles a sub-node of the xml tree.
	 * 
	 * @return the XmlNode of the subtree
	 */
	private XmlNode handleNode() {
		checkNextTokenType(TokenType.OPENANGLEBRACKETTOKEN);
		
		if(isNextTypeEqual(TokenType.SLASHTOKEN)) return null;
		
		checkTokenType(TokenType.IDENTIFIERTOKEN);
		XmlNode node = new XmlNode(currentToken.getContent());
		
		while(!isNextTypeEqual(TokenType.CLOSEDANGLEBRACKETTOKEN)) {
			if(isTypeEqual(TokenType.SLASHTOKEN)) {
				checkNextTokenType(TokenType.CLOSEDANGLEBRACKETTOKEN);
				return node;
			}
			
			checkTokenType(TokenType.IDENTIFIERTOKEN);
			String identifier = currentToken.getContent();
			checkNextTokenType(TokenType.EQUALTOKEN);
			checkNextTokenType(TokenType.STRINGTOKEN);
			if(!node.addProperties(identifier, currentToken.getContent()))
				throw new ParserException("The property " + identifier +  " was used twice in the node " + node.getIdentifier());
		}
		
		if(getLookAheadToken(1).getType() == TokenType.CONTENTTOKEN) {
			getNextToken();
			node.setContent(currentToken.getContent());
		}
		
		XmlNode child = null;
		do {
			child = handleNode();
		} while (node.addChild(child));
		
//		checkNextTokenType(TokenType.OPENANGLEBRACKETTOKEN);
//		checkNextTokenType(TokenType.SLASHTOKEN);
		checkNextToken(TokenType.IDENTIFIERTOKEN, node.getIdentifier());
		checkNextTokenType(TokenType.CLOSEDANGLEBRACKETTOKEN);
		
		return node;
	}
	
	
	
	/************************************Utility Functions*******************************************************/
	
	private boolean isTokenEqual(Token token) {
		return currentToken.equals(token);
	}

	private boolean isNextTokenEqual(Token token) {
		getNextToken();
		return isTokenEqual(token);
	}
	
	private boolean isTokenEqual(TokenType type, String content) {
		return currentToken.getType() == type && currentToken.getContent().equals(content);
	}
	
	private boolean isNextTokenEqual(TokenType type, String content) {
		getNextToken();
		return isTokenEqual(type, content);
	}
	
	private boolean isTypeEqual(TokenType type) {
		return currentToken.getType() == type;
	}
	
	private boolean isNextTypeEqual(TokenType type) {
		getNextToken();
		return isTypeEqual(type);
	}

	private boolean isContentEqual(String content) {
		return currentToken.getContent().equals(content);
	}

	private boolean isNextContentEqual(String content) {
		getNextToken();
		return isContentEqual(content);
	}
	
	private void checkToken(Token token) {
		if (!isTokenEqual(token))
			throwException(token.getContent());
	}

	private void checkNextToken(Token token) {
		getNextToken();
		checkToken(token);
	}
	
	private void checkToken(TokenType type, String content) {
		if (!isTokenEqual(type, content))
			throwException(content);
	}

	private void checkNextToken(TokenType type, String content) {
		getNextToken();
		checkToken(type, content);
	}
	
	private void checkTokenType(TokenType type) {
		if (!isTypeEqual(type))
			throwException(type.toString());
	}
	
	private void checkNextTokenType(TokenType typeToCheckWith) {
		getNextToken();
		checkTokenType(typeToCheckWith);
	}
	
	private void checkTokenContent(String content) {
		if(!isContentEqual(content))
			throwException(content);
	}

	private void checkNextTokenContent(String contentToCheckWith) {
		getNextToken();
		checkTokenContent(contentToCheckWith);
	}
	
	private void throwException(String expectedString) {
		throw new ParserException(
				"The Parser found " + currentToken.getContent() + " but expected " + expectedString + ".");
	}
	
	private Token getNextToken() {
		currentToken = listOfTokens.get(++tokenListIndex);
		if(!isTypeEqual(TokenType.EOFTOKEN))
			return currentToken;
		throw new ParserException("Parser found nothing more. The programm is not complete.");
	}
	
	private Token getCurrentTokenAndGoOn() {
		Token tempToken = currentToken;
		getNextToken();
		return tempToken;
	}
	
	private Token getLookAheadToken(int n) {
		if (tokenListIndex + n < listOfTokens.size() && 0 <= tokenListIndex + n) 
			return listOfTokens.get(tokenListIndex + n);
		
		throw new ParserException("Parser found nothing more. The programm is not complete.");
	}
}
