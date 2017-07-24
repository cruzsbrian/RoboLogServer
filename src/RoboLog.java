import java.util.HashMap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.google.gson.Gson;

public class RoboLog {
	
	static Server server;
	static boolean serverRunning = false;
	
	static Gson gson = new Gson();
	
	static HashMap<String, Object> buffer = new HashMap<String, Object>();
	
	/**
	 * Start the server on specified port<br>
	 * @param port
	 */
	public static void startServer(int port) {
		serverRunning = false;
		
		server = new Server(port);
		
		WebSocketHandler wsHandler = new WebSocketHandler() {
			public void configure(WebSocketServletFactory factory) {
				factory.register(Handler.class);
			}
		};
		
		server.setHandler(wsHandler);
		
		try {
			
			server.start();
			server.join();
			serverRunning = true;
			
		} catch (Exception e) {
			
			System.out.println("Error: could not start server.");
			System.out.println(e.getMessage());
			e.printStackTrace();
			
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
	 */
	public static void send() {
		String jsonStr = gson.toJson(buffer);
		
	}

}
