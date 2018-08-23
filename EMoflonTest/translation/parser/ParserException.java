package translation.parser;

import translation.TranslationException;

/**
 * Exception of the parser.
 * 
 * @author Timo Freitag
 *
 */
public class ParserException extends TranslationException {

	/**
	 * Constructs a ParserException with a given message.
	 * 
	 * @param msg message
	 */
	public ParserException(String msg) {
		super(msg);
	}
	
}
