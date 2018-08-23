package translation;

import java.util.List;

import translation.lexer.LexerException;
import translation.lexer.Token;

public interface Lexer {
	public List<Token> getTokenList();
}
