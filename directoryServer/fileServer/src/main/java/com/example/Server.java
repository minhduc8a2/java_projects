package com.example;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final static int PORT = 8080;

    public void start(){
        start(PORT);
    }
    public void start(Integer port) {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = server.accept();
                new Thread(new RequestHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
