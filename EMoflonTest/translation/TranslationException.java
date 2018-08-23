/**
 * 
 */
package translation;

/**
 * Exception of the translation.
 * 
 * @author Timo Freitag
 *
 */
public class TranslationException extends RuntimeException {
	/**
	 * Constructs a TranslationException with a given message
	 * 
	 * @param msg message
	 */
	public TranslationException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructs a TranslationException with a given exception
	 * 
	 * @param e exception
	 */
	public TranslationException(Exception e) {
		super(e);
	}
}
