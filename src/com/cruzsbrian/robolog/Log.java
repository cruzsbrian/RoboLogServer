package com.cruzsbrian.robolog;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Log extends Thread {

    private static LogServer logserver = null;

    private static Gson gson = new Gson();

    // buffers to hold data waiting to be sent
    private static JsonArray bufferGraph = new JsonArray();
    private static JsonArray bufferLog = new JsonArray();

    private static long startTime;

    // delay between sending batches of data
    private static int batchDelay = 50;

    /**
     * Start the server on a specified port
     * @param port
     */
    public static void startServer(int port) {
        if (logserver == null) {
            logserver = new LogServer(port);
            logserver.start();

            startTime = System.currentTimeMillis();
            
            // start the data sending
            (new Log()).start();
        } else {
            printRoboLog();
            System.out.println("Error: already started server");
        }
    }
    
    /**
     * Set the delay between batches of graph data<br>
     * A longer delay puts less load on your RoboRIO
     * @param delay
     */
    public static void setDelay(int delay) {
    	batchDelay = delay;
    }

    /**
     * Add a key, value pair to send to the client
     * @param key
     * @param value
     */
    public static void add(String key, Double value) {
    	JsonObject dataPoint = new JsonObject();

        // add the current time
        dataPoint.addProperty("t", getCurrentTime());
    	
        // add the user's data
        dataPoint.addProperty(key, value);
        
        // append to the buffer
        bufferGraph.add(dataPoint);
    }

    /**
     * Add a log to send to the client<br>
     * startServer() must be called before this
     * @param subject
     * @param msg
     */
    public static void log(String subject, Object msg) {
    	JsonObject logData = new JsonObject();
        
        // add the current time
        logData.addProperty("t", getCurrentTime());
        
        // add the user's data
        logData.addProperty("subject", subject);
        logData.addProperty("msg", msg.toString());

        bufferLog.add(logData);
    }

    /**
     * Send the data and clear the buffer to begin adding data for the next batch<br>
     * startServer() must be called before calling this
     * @param time
     */
    private static void send() {
    	if (bufferGraph.size() > 0 || bufferLog.size() > 0) {
	        // hashmap with the data type (for the client to know what it's receiving)
	        HashMap<String, Object> data = new HashMap<String, Object>();
	        data.put("type", "data");
	        
	        // Graph data
			// copy the buffer to avoid concurrent modification errors
			JsonArray copyOfBuffer = new JsonArray();
			copyOfBuffer.addAll(bufferGraph);
			
	        // clear the buffer for the next batch
	        bufferGraph = new JsonArray();
	        data.put("graph", copyOfBuffer);
		
	        // Log data
			// copy the buffer to avoid concurrent modification errors
			copyOfBuffer = new JsonArray();
			copyOfBuffer.addAll(bufferLog);
			
	        // clear the buffer for the next batch
	        bufferLog = new JsonArray();
	        data.put("log", copyOfBuffer);
	    	
	    	sendAsJson(data);
    	}
    }

    /**
     * Convert object to JSON and send it through logserver
     * @param data
     */
    protected static void sendAsJson(Object data) {
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
    
    /**
     * Returns time in seconds since log was started
     * @return
     */
    public static double getCurrentTime() {
    	return ((double) (System.currentTimeMillis() - startTime)) / 1000;
    }

    /*
     * Print "[RoboLog]". Should be called before printing any status message
     */
    protected static void printRoboLog() {
        System.out.print("[RoboLog] ");
    }
    
    public void run() {
    	while (true) {
    		try {
				sleep(batchDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		send();
    	}
    }

}
