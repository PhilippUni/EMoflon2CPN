package translation.lexer;

import translation.TranslationException;

/**
 * Exception of the lexer
 * 
 * @author Timo Freitag
 *
 */
public class LexerException extends TranslationException {
	/**
	 * Constructs a LexerException with a given message
	 * 
	 * @param msg message
	 */
	public LexerException(String msg) {
		super(msg);
	}
}
