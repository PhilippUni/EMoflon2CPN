/**
 * 
 */
package simulation;

import java.io.IOException;

/**
 * @author Timo Freitag
 *
 */
public class SimulationException extends RuntimeException {
	public SimulationException(String msg) {
		super(msg);
	}

	public SimulationException(Exception e) {
		super(e);
	}
}
