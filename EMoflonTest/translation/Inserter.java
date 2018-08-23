/**
 * 
 */
package translation;

import java.io.IOException;

import translation.lexer.LexerException;
import translation.parser.ParserException;
import translation.parser.XmlNode;

/**
 * @author Timo Freitag
 *
 */
public interface Inserter {
	public XmlNode getTree();
}
