package translation.lexer;

/**
 * Range between two chars.
 * 
 * @author Timo Freitag
 *
 */
public class CharRange {
	private char start;
	private char end;
	
	/**
	 * Constructs a char range
	 * 
	 * @param start start char
	 * @param end end char
	 */
	public CharRange(char start, char end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Returns the start of the char range.
	 * 
	 * @return start char
	 */
	public char getStart() {
		return this.start;
	}
	
	/**
	 * Return the end of the char range.
	 * 
	 * @return end char
	 */
	public char getEnd() {
		return this.end;
	}
}
