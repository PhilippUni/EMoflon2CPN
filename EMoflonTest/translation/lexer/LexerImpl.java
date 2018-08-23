package translation.lexer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import translation.Lexer;
import translation.TranslationException;

/**
 * This lexer does a lexical analysis of a given String.
 * It is able do return a List of Tokens which are of a specific TokenType.
 * The lexer tries to handle all kind of xml-code.
 * 
 * 
 * @author Timo Freitag
 *
 */
public class LexerImpl implements Lexer {

	private String input;
	private char currentChar;
	private int currentPosition = -1;
	private boolean lastBracketSeen = false; //false is closed, true is open
	
	public final static char EOF = (char) -1;

	private ArrayList<Token> tokens;

	private LinkedHashMap<Matcher, TokenGetter> tokenMap = new LinkedHashMap<Matcher, TokenGetter>();

	private CharMatcher whitespaceMatcher = new CharMatcher(' ', '\n', '\t', '\r');
	private CharMatcher slashMatcher = new CharMatcher('/');
	private CharMatcher openAngleBracketMatcher = new CharMatcher('<');
	private CharMatcher closedAngleBracketMatcher = new CharMatcher('>');
	private CharMatcher equalMatcher = new CharMatcher('=');
	private CharMatcher identifierMatcher = new CharMatcher(new CharRange('a', 'z'), new CharRange('A', 'Z'), new CharRange('0', '9'), new CharRange(':', ':'));
	private CharMatcher quotationMatcher = new CharMatcher('"');

	/**
	 * Constructs a Lexer which is able to handle xml-code.
	 * 
	 * @param text input string to analyse
	 */
	public LexerImpl(String text) {
		this.input = text + EOF;
	}

	/**
	 * Returns the next char of the input string and increments the currentPosition.
	 * This method does not need to check if it is out bounds
	 * because the lexer handles the EOF token and stops lexing.
	 * 
	 * @return next char of the string
	 */
	private char getNextChar() {
		currentChar = input.charAt(++currentPosition);
		return currentChar;
	}
	
	/**
	 * Returns a char which got a given offset to the currentPosition
	 * This method checks if the index is in the bounds of the input string
	 * If not it throws a LexerException
	 * 
	 * @param n offset
	 * @return the char with the offset to the currentPosition
	 */
	private char getLookaheadChar(int n) {
		if(currentPosition + n < input.length() && 0 <= currentPosition + n)
			currentChar = input.charAt(currentPosition + n);
		else 
			throw new LexerException("Out of bounds lookahead: " + currentChar + "(" + currentPosition + ") + " + n);
		return currentChar;
	}

	/**
	 * Returns the currentChar and increments the currentPosition afterwards.
	 * This method does not need to check if it is out bounds
	 * because the lexer handles the EOF token and stops lexing.
	 * 
	 * @return currentChar before the increment
	 */
	private char getCurrentCharAndGoOn() {
		char tempChar = input.charAt(currentPosition);
		currentChar = input.charAt(++currentPosition);
		return tempChar;
	}

	@Override
	public List<Token> getTokenList() throws TranslationException  {
		if (tokens == null) {
			tokens = new ArrayList<Token>();
			startLexing();
		}
		return tokens;
	}
	
	/**
	 * Starts the lexing procedure.
	 * It analysis the input string with regard to a xml-code
	 * and devide it into different Tokens with a specific TokenType.
	 * It takes care when the last char of the string occurs.
	 */
	private void startLexing() {
		initializeTokenMap();
		getNextChar();
		boolean illegalCharFound = true;

		while (currentChar != EOF) {
			illegalCharFound = true;

			for (Matcher matcher : tokenMap.keySet()) {	
				if (matcher.match(currentChar)) {
					Token tokenToAdd = tokenMap.get(matcher).getToken(null);
					if (tokenToAdd != null)
						tokens.add(tokenToAdd);
					illegalCharFound = false;

					break;
				}
			}
			
			if (illegalCharFound)
				throw new LexerException(
						"The Lexer found the char: '" + currentChar + "', but he isn't able to handle it.");
		}
		tokens.add(new Token(TokenType.EOFTOKEN, "" + EOF));
	}
	
	/**
	 * Initializes the tokenMap with handler functions.
	 */
	private void initializeTokenMap() {
		tokenMap.put(this::contentMatcher, this::handleContent);
		tokenMap.put(whitespaceMatcher, this::handleWhitespace);
		tokenMap.put(slashMatcher, this::handleSlash);
		tokenMap.put(openAngleBracketMatcher, this::handleOpenAngleBracket);
		tokenMap.put(closedAngleBracketMatcher, this::handleClosedAngleBracket);
		tokenMap.put(equalMatcher, this::handleEqual);
		tokenMap.put(identifierMatcher, this::handleIdentifier);
		tokenMap.put(quotationMatcher, this::handleString);
	}

	/**
	 * Handles whitespace.
	 * Allways return null because whitespace is not a token.
	 * 
	 * @param builder StringBuilder for a content(not needed).
	 * @return null
	 */
	private Token handleWhitespace(StringBuilder builder) {
		getNextChar();
		return null;
	}
	
	/**
	 * Handles a slash
	 * @param builder StringBuilder for a content(not needed).
	 * @return SlashToken
	 */
	private Token handleSlash(StringBuilder builder) {
		return new Token(TokenType.SLASHTOKEN, Character.toString(getCurrentCharAndGoOn()));
	}
	
	/**
	 * Handles open angle bracket
	 * Returns null if the next char is a '!' or '?' and skips to the point when the bracket is closed.
	 * 
	 * @param builder StringBuilder for a content(not needed).
	 * @return Token of TokenType.OPENANGLEBRACKETTOKEN
	 */
	private Token handleOpenAngleBracket(StringBuilder builder) {
		char lookahead = getLookaheadChar(1);
		if(lookahead == '!'|| lookahead == '?') {
			getNextChar();
			while(!closedAngleBracketMatcher.match(currentChar) &&  currentChar!= EOF) {
				getNextChar();
			}
			getNextChar();
			return null;
		}
		lastBracketSeen = true;
		return new Token( TokenType.OPENANGLEBRACKETTOKEN, Character.toString(getCurrentCharAndGoOn()));
	}
	
	/**
	 * Handles closed angle bracket.
	 * @param builder StringBuilder for a content(not needed).
	 * @return Token of TokenType.CLOSEANGLEBRACKETTOKEN
	 */
	private Token handleClosedAngleBracket(StringBuilder builder) {
		lastBracketSeen = false;
		return new Token(TokenType.CLOSEDANGLEBRACKETTOKEN, Character.toString(getCurrentCharAndGoOn()));
	}
	
	/**
	 * Handles equal.
	 * @param builder StringBuilder for a content(not needed).
	 * @return Token of TokenType.EQUALTOKEN
	 */
	private Token handleEqual(StringBuilder builder) {
		return new Token(TokenType.EQUALTOKEN, Character.toString(getCurrentCharAndGoOn()));
	}
	
	/**
	 * Handles an identifier which is combination of chars, integers and colons.
	 * @param builder StringBuilder to build the identifier.
	 * @return Token of TokenType.IDENTIFIERTOKEN
	 */
	private Token handleIdentifier(StringBuilder builder) {
		if(builder == null) builder = new StringBuilder();
		if(identifierMatcher.match(currentChar))
			return handleIdentifier(builder.append(getCurrentCharAndGoOn()));
		
		return new Token(TokenType.IDENTIFIERTOKEN, builder.toString());
	}
	
	/**
	 * Handles a string.
	 * @param builder StringBuilder to build the content of the string.
	 * @return Token of TokenType.STRINGTOKEN
	 */
	private Token handleString(StringBuilder builder) {
		if(currentChar == EOF) throw new LexerException("String was not closed.");
		if(builder == null) builder = new StringBuilder();
		
		if(!quotationMatcher.match(currentChar) || builder.length() == 0)
			return handleString(builder.append(getCurrentCharAndGoOn()));
		
		getNextChar();
		return new Token(TokenType.STRINGTOKEN, builder.substring(1));
	}
	
	/**
	 * Tries to match a given char.
	 * It will be matched if it is not in angle brackets.
	 * @param charToMatch Char which should be matched
	 * @return success of the match.
	 */
	private boolean contentMatcher(char charToMatch) {
		return !lastBracketSeen && !openAngleBracketMatcher.match(currentChar) && !whitespaceMatcher.match(currentChar);
	}
	
	/**
	 * Handles content which is every kind of char which is not in angle brackets
	 * @param builder StringBuilder to build content.
	 * @return Token of TokenType.CONTENTTOKEN
	 */
	private Token handleContent(StringBuilder builder) {
		boolean lastWhiteSpace = false;
		if(builder == null) builder = new StringBuilder();
		while(!openAngleBracketMatcher.match(currentChar) &&  currentChar!= EOF) {
			if(whitespaceMatcher.match(currentChar)) {
				if(!lastWhiteSpace) builder.append(currentChar); 					
				lastWhiteSpace = true;
			} else {
				builder.append(currentChar);
				lastWhiteSpace = false;
			}
			getNextChar();
		}
		return new Token(TokenType.CONTENTTOKEN, builder.toString());
	}
	
	/**
	 * Provides a functional interface to create a token with a StringBuilder to build the content of the token
	 * @author Timo Freitag
	 *
	 */
	private interface TokenGetter {
		/**
		 * Creates a Token.
		 * StringBuilder builds the content of the token.
		 * 
		 * @param builder StringBuilder to build content of the token.
		 * @return token
		 * @throws LexerException is thrown when something fails while lexing a token.
		 */
		public Token getToken(StringBuilder builder) throws LexerException;
	}
}
