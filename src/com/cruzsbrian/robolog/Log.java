package com.cruzsbrian.robolog;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Log extends Thread {

    private static LogServer logserver = null;

    private static Gson gson = new Gson();

    private static JsonArray buffer = new JsonArray();

    private static long startTime;
    
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
    	
    	// calculate current time in seconds
        double t = ((double) (System.currentTimeMillis() - startTime)) / 1000;

        // add the current time
        dataPoint.addProperty("t", t);
    	
        // add the user's data
        dataPoint.addProperty(key, value);
        
        // append to the buffer
        buffer.add(dataPoint);
    }

    /**
     * Send the data and clear the buffer to begin adding data for the next batch<br>
     * startServer() must be called before calling this
     * @param time
     */
    private static void send() {
    	if (buffer.size() > 0) {
    		// copy the buffer to avoid concurrent modification errors
    		JsonArray copyOfBuffer = new JsonArray();
    		copyOfBuffer.addAll(buffer);
    		
	        // clear the buffer for the next batch
	        buffer = new JsonArray();
    		
	        // put the buffer in a hashmap along with the data type (for the client to know what it's receiving)
	        HashMap<String, Object> data = new HashMap<String, Object>();
	        data.put("type", "graph");
	        data.put("obj", copyOfBuffer);
	
	        sendAsJson(data);
    	}
    }

    /**
     * Send a log to the dashboard<br>
     * startServer() must be called before this
     * @param subject
     * @param msg
     */
    public static void log(String subject, String msg) {
        // calculate current time in seconds
        double t = ((double) (System.currentTimeMillis() - startTime)) / 1000;

        // make a hashmap to hold all the log info
        HashMap<String, Object> log = new HashMap<String, Object>();
        log.put("t", t);
        log.put("subject", subject);
        log.put("msg", msg);

        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("type", "log");
        data.put("obj", log);

        sendAsJson(data);
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
