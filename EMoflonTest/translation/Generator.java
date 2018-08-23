/**
 * 
 */
package translation;

/**
 * Provides an interface for a code generator for xml-files.
 * 
 * @author Timo Freitag
 *
 */
public interface Generator {
	/**
	 * Generates code for a xml-file from a given XmlTree
	 * 
	 * @param node Given XmlTree 
	 * @return xml Code
	 */
	public String generateCode();
}
