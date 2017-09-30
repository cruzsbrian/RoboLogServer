import java.util.HashMap;

import com.google.gson.Gson;

public class Log {
	
	private static LogServer logserver = null;
	
	private static Gson gson = new Gson();
	
	private static HashMap<String, Object> buffer = new HashMap<String, Object>();
	
	private static long startTime;
	
	/**
	 * Start the server on a specified port
	 * @param port
	 */
	public static void startServer(int port) {
		if (logserver == null) {
			logserver = new LogServer(port);
			logserver.start();
			
			startTime = System.currentTimeMillis();
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
	 * startServer() must be called before calling this
	 * @param time
	 */
	public static void send() {
		// calculate current time in seconds
		double t = ((double) (System.currentTimeMillis() - startTime)) / 1000;
		
		// add the current time to the data buffer
		buffer.put("t", t);
		
		// put the buffer in a hashmap along with the data type (for the client to know what it's receiving)
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("type", "graph");
		data.put("obj", buffer);
		
		sendAsJson(data);
	}
	
	/**
	 * Send a log to the dashboard<br>
	 * startServer() must be called before this
	 * @param caller
	 * @param msg
	 */
	public static void log(String subject, String msg) {
		// calculate current time in seconds
		double t = ((double) (System.currentTimeMillis() - startTime)) / 1000;
		data.put("type", "log");
		data.put("sender", caller.getClass().getSimpleName());
		data.put("msg", msg);
		
		sendAsJson(data);
	}
	
	/**
	 * Convert object to JSON and send it through logserver
	 * @param data
	 */
	private static void sendAsJson(Object data) {
		String jsonStr = gson.toJson(data);
		
		if (logserver.isConnected()) {
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
