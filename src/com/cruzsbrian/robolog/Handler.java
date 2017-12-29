package com.cruzsbrian.robolog;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

        Constants.sendConstants();
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
    public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {}

    @Override
    public void onWebSocketText(String msg) {
        JsonParser parser = new JsonParser();

        JsonElement root = parser.parse(msg);
        if (root.isJsonObject()) {
            JsonObject data = root.getAsJsonObject();

            String dataType = data.get("type").getAsString();

            Log.printRoboLog();
            System.out.println("Received " + dataType + " from client");
            
            // if receiving constants
            if (dataType.equals("constants")) {
                JsonArray constants = data.getAsJsonArray("constants");
                Constants.addAll(constants);

                // store the constants as defaults if needed
                boolean makeDefaults = data.get("defaults").getAsBoolean();
                if (makeDefaults) {
                    Constants.writeToFile();
                }
            }
            
            // if receiving a request
            if (dataType.equals("request")) {
            	String requestedName = data.get("requestedName").getAsString();
            	
            	// if requesting (default) constants, recall and then send
            	if (requestedName.equals("constants")) {
            		Constants.loadFromFile();
            		Constants.sendConstants();
            	}
            }
        } else {
            Log.printRoboLog();
            System.out.println("Error parsing JSON from client:");
            System.out.println(msg);
        }
    }

}
