import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

public class Handler implements WebSocketListener {
	
	static Session session;
	static boolean connected = false;
	
	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		Log.printRoboLog();
		System.out.println("Connection closed");
		
		connected = false;
	}
	
	@OnWebSocketError
	public void onWebSocketError(Throwable t) {
		Log.printRoboLog();
		System.out.println("Error: " + t.getMessage());
	}
	
	@OnWebSocketConnect
	public void onWebSocketConnect(Session s) {
		Log.printRoboLog();
		System.out.println("Connection opened with client");
		
		session = s;
		connected = true;
	}
	
	@OnWebSocketMessage
	public void onWebSocketMessage(String msg) {}
	
	public static void push(String msg) throws Exception {
		session.getRemote().sendString(msg);
	}
	
	public static boolean isConnected() {
		return connected;
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWebSocketText(String arg0) {
		// TODO Auto-generated method stub
		
	}


}
