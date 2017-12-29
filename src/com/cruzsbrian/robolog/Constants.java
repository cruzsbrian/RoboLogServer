package com.cruzsbrian.robolog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;

public class Constants {

    public enum Types {
        INT,
        DOUBLE,
        STRING,
        OBJECT
    }

    private static Properties constants = new Properties();
    private static String constantsFileName = "robolog-constants.properties";

    public static int getAsInt(String key) {
        String val = constants.getProperty(key);
        
        // getProperty returns null if it can't find it
        if (val == null) {
        	Log.printRoboLog();
        	System.out.println("Constant for key '" + key + "' was not found. Returning 0");
        	return 0;
        }
        
        // attempt to convert to an integer
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            Log.printRoboLog();
            System.out.println("Couldn't convert " + val + " to an integer. Returning 0");
            return 0;
        }
    }

    public static double getAsDouble(String key) {
        String val = constants.getProperty(key);
        
        // getProperty returns null if it can't find it
        if (val == null) {
        	Log.printRoboLog();
        	System.out.println("Constant for key '" + key + "' was not found. Returning 0");
        	return 0;
        }
        
        // attempt to convert to a double
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            Log.printRoboLog();
            System.out.println("Couldn't convert " + val + " to a double. Returning 0");
            return 0;
        }
    }

    public static String getAsString(String key) {
        return constants.getProperty(key);
    }

    public static void add(String key, Object val) {
        constants.setProperty(key, val.toString());
    }

    protected static void addAll(JsonArray list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());

            JsonObject constant = list.get(i).getAsJsonObject();

            add(constant.get("key").getAsString(), constant.get("val").getAsString());
        }
    }

    public static void loadFromFile() {
        try {

            InputStream input = new FileInputStream(constantsFileName);
            constants.load(input);

            Log.printRoboLog();
            System.out.println("Successfully read constants from " + constantsFileName);

        } catch (FileNotFoundException e) {
            Log.printRoboLog();
            System.out.println("Couldn't find constants file. Will make a new one once defaults are set.");
        } catch (IOException e) {
            Log.printRoboLog();
            System.out.println("Error reading constants file.");
            e.printStackTrace();
        }
    }

    public static void writeToFile() {
        try {

            OutputStream output = new FileOutputStream(constantsFileName);
            constants.store(output, null);

            Log.printRoboLog();
            System.out.println("Successfully wrote constants to " + constantsFileName);

        } catch (FileNotFoundException e) {
            Log.printRoboLog();
            System.out.println("Unable to find or create constants file.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.printRoboLog();
            System.out.println("Unable to write to constants file.");
            e.printStackTrace();
        }
    }

    protected static void sendConstants() {
        // create list of constants as objects
        List<Constant> constantsList = new ArrayList<Constant>();
        for (String key : constants.stringPropertyNames()) {
            Constant c = new Constant();
            c.key = key;
            c.val = getAsString(key);
            constantsList.add(c);
        }

        // create constants object
        ConstantsSend cs = new ConstantsSend();
        cs.obj = constantsList;

        // convert to JSON and send
        Log.sendAsJson(cs);
    }

    protected static void print() {
        System.out.println(constants.toString());
    }

    // format of json sent
    static class ConstantsSend {
        String type = "constants";
        List<Constant> obj;
    }

    // format of one constant in json
    static class Constant {
        String key, val;
    }

}
