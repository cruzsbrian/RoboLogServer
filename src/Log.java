import java.util.HashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.google.gson.Gson;

public class Log {
	
	private static LogServer logserver = null;
	
	private static Gson gson = new Gson();
	
	private static HashMap<String, Object> buffer = new HashMap<String, Object>();
	
	public static void connect(int port) {
		if (logserver == null) {
			logserver = new LogServer(port);
			logserver.start();
		} else {
			printRoboLog();
			System.out.println("Error: already started server");
		}
	}
	
	/**
	 * Add a key, value pair to send to the client
	 * @param key
	 * @param value
	 */
	public static void add(String key, Object value) {
		buffer.put(key, value);
	}
	
	/**
	 * Send the data and clear the buffer to begin adding data for the next batch<br>
	 * connect() must be called before calling this
	 */
	public static void send() {
		if (logserver.isConnected()) {
			String jsonStr = gson.toJson(buffer);
			
			try {
				logserver.send(jsonStr);
			} catch (Exception e) {
				printRoboLog();
				System.out.println("Error sending data");
				e.printStackTrace();
			}
		} else {
			printRoboLog();
			System.out.println("Error: server is not yet connected");
		}
	}
	
	/*
	 * Print "[RoboLog]" in blue. Should be called before printing any status message
	 */
	protected static void printRoboLog() {
		System.out.print(Colors.ANSI_CYAN + "[RoboLog] " + Colors.ANSI_RESET);
	}

}
