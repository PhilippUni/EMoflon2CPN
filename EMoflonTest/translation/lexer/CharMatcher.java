package translation.lexer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Char matcher which is able to handle different types of matches.
 * For example: a single char, multiple chars(array of chars), char ranges
 * 
 * @author Timo Freitag
 *
 */
public class CharMatcher implements Matcher{
	
	private LinkedList<AbstractMatcher> matcher = new LinkedList<AbstractMatcher>();
	
	/**
	 * Constructs a single char matcher
	 * 
	 * @param c char to match with
	 */
	public CharMatcher(char c) {
		this.matcher.add(new SingleCharMatcher(c));
	}
	
	/**
	 * Constructs an array char matcher.
	 * 
	 * @param c chars to match with
	 */
	public CharMatcher(char... c) {
		this.matcher.add(new ArrayCharMatcher(c));
	}
	
	/**
	 * Constructs a range char matcher.
	 * 
	 * @param range range of chars to match with
	 */
	public CharMatcher(CharRange... ranges) {
		Arrays.stream(ranges).forEach(range -> matcher.add(new RangeCharMatcher(range)));
	}
	
	public boolean match(char charToMatch) {
		return matcher.stream().filter(m -> m.match(charToMatch)).count() >= 1;
	}
	
	
	/**
	 * Abstract class to represent an intern char matcher.
	 * 
	 * @author Timo Freitag
	 *
	 */
	private abstract class AbstractMatcher {
		/**
		 * Matches a given char with one, an array or a range of chars.
		 * 
		 * @param c char to match
		 * @return success of the match
		 */
		public abstract boolean match(char c);
		
	}
	
	/**
	 * Char matcher which is able to match only one char.
	 * 
	 * @author Timo Freitag
	 *
	 */
	private class SingleCharMatcher extends AbstractMatcher {
		private char c;
		
		/**
		 * Constructs a single char matcher.
		 * 
		 * @param c char to match with
		 */
		public SingleCharMatcher(char c) {
			this.c = c;
		}
		
		public boolean match(char charToMatch) {
			return this.c == charToMatch;
		}
	}
	
	/**
	 * Char matcher which is able to match the chars in the given array.
	 * 
	 * @author Timo Freitag
	 *
	 */
	private class ArrayCharMatcher extends AbstractMatcher{
		private char[] c;
		
		/**
		 * Constructs an array char matcher.
		 * 
		 * @param c array of chars to match with
		 */
		public ArrayCharMatcher(char[] c) {
			this.c = Arrays.copyOf(c, c.length);
		}
		
		public boolean match(char charToMatch) {
			for(int i = 0; i < c.length; i++) {
				if(c[i] == charToMatch)
					return true;
			}
			return false;
		}
	}
	
	/**
	 * Char matcher which is able to match the chars within the given range.
	 * 
	 * @author Timo Freitag
	 *
	 */
	private class RangeCharMatcher extends AbstractMatcher {
		private CharRange range;
		
		/**
		 * Constructs a range char matcher.
		 * 
		 * @param range range of chars to match with
		 */
		public RangeCharMatcher(CharRange range) {
			this.range = range;
		}
		
		public boolean match(char charToMatch) {
			return range.getStart() <= charToMatch && charToMatch <= range.getEnd();
		}
	}
	
	
	
}