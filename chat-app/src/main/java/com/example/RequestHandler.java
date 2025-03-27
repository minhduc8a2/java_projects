package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Set;

public class RequestHandler implements Runnable {

    private Socket clientSocket;
    private Set<PrintWriter> clientWriters;
    private PrintWriter out;
    private BufferedReader in;

    public RequestHandler(Socket clientSocket, Set<PrintWriter> clientWriters) {
        this.clientSocket = clientSocket;
        this.clientWriters = clientWriters;
    }

    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(10000);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(out);
            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("📩 Received: " + message + " from " + clientSocket.getInetAddress());
                broadcastMessage(message);
            }

        } catch (SocketTimeoutException e) {
            System.out.println("⏳ Client inactive for too long, disconnecting: " + clientSocket.getInetAddress());
        } catch (Exception e) {
            System.out.println("Client request errors: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            clientWriters.remove(out);
        }

    }

    private void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter printWriter : clientWriters) {
                printWriter.println(message);
            }
        }
    }

}
