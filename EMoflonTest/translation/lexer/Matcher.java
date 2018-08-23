package translation.lexer;

/**
 * Provides an interface for a match with char
 * @author Timo Freitag
 *
 */
public interface Matcher {
	/**
	 * Tries to match the given char with the matcher.
	 * 
	 * @param charToMatch char to match
	 * @return success of the match
	 */
	public boolean match(char charToMatch);
}
