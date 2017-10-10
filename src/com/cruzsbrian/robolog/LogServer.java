package com.cruzsbrian.robolog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class LogServer extends Thread {

    private Server server;
    private int port;

    public LogServer(int p) {
        port = p;
    }

    public void run() {
        Log.printRoboLog();
        System.out.println("Server started");

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

        } catch (Exception e) {

            Log.printRoboLog();
            System.out.println("Error: server failed to connect on port " + port);
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    public boolean isConnected() {
        return Handler.isConnected();
    }

    public void send(String msg) throws Exception {
        Handler.push(msg);
    }

}
