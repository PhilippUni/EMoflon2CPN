package translation.lexer;

/**
 * Token of a specific token type with a content.
 * 
 * @author Timo Freitag
 *
 */
public class Token {
	private TokenType type;
	private String content;

	/**
	 * Constructs a Token.
	 * 
	 * @param type type of the token
	 * @param content content of the token
	 */
	public Token(TokenType type, String content) {
		this.content = content;
		this.type = type;
	}
	
	/**
	 * Returns the type of the token
	 * 
	 * @return type
	 */
	public TokenType getType() {
		return this.type;
	}
	
	/**
	 * Returns the content of the Token
	 * 
	 * @return content
	 */
	public String getContent() {
		return this.content;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [content=" + content + ", type=" + type + "]";
	}
}
