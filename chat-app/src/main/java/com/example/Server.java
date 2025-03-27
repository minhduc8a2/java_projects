package com.example;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private static final int PORT = 8080;
    private Set<PrintWriter> clientWriters = new HashSet<>();

    public void start() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = server.accept();
                new Thread(new RequestHandler(clientSocket, clientWriters)).start();

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
