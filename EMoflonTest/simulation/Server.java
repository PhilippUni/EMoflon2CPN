/**
 * 
 */
package simulation;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import javacpn.EncodeDecode;
import javacpn.JavaCPN;
import javacpn.JavaCPNInterface;
import translation.Translation;

/**
 * @author Timo Freitag
 *
 */
public class Server extends Thread {
	private int port;
	private Map<String, BooleanSupplier> functions = new HashMap<String, BooleanSupplier>();
	private JavaCPNInterface cpn = new JavaCPN();
	private String path;
	private Simulation simulation = new Simulation();
	private BiConsumer<String, Boolean> writer;
	private List<String> logList;
	
	public Server(int port, BiConsumer<String, Boolean> writer, List<String> logList) {
		this.writer = writer;
		this.port = port;
		this.logList = logList;
	}
	
	public void run() {
		try {
			logList.clear();
			simulation.initialize();
			cpn.connect("localhost", port);
			writer.accept("Connected to CPN Tools.", false);
			polling();
			cpn.disconnect();
		} catch (IOException e) {
			writer.accept("Failed to connect to CPN Tools.", true);
		}
		catch(DisconnectedException e) {
			writer.accept("Disconnected from CPN Tools.", true);
		}
		catch(RuntimeException e) {
			writer.accept("Something went wrong.", true);
		}
		writer.accept("If you wannt to log your simulation result, you can save it via the log-button.", true);
	}
	
	private void polling() {
		boolean finished = false;
		try {
			while(!finished) {
				String msg = EncodeDecode.decodeString(cpn.receive());
				boolean result = false;		
				result = simulation.invoke(msg);
				logList.add(msg + ' ' + result);
				writer.accept("ActivityNode: \"" + msg + "\" Result: \"" + result + "\"", false);
				cpn.send(EncodeDecode.encode(Boolean.toString(result)));
			}
		}
		catch(IOException e) {
			simulation.close();
			throw new DisconnectedException("CPN Tools disconnected.");
		}
	}
	
	
}
