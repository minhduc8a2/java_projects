package com.example;

import java.io.BufferedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {
    private static final int PORT = 8080;
    private final ObjectMapper mapper = new ObjectMapper();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try (BufferedOutputStream buff = new BufferedOutputStream(clientSocket.getOutputStream())) {
                        List<Employee> employees = employeeDAO.findAll();
                        String json = mapper.writeValueAsString(employees);
                        buff.write("http 1.1 200 OK\r\n".getBytes());
                        buff.write("Content-Type:application/json\r\n".getBytes());
                        buff.write(("Content-Length:" + json.getBytes().length + "\r\n").getBytes());
                        buff.write("\r\n".getBytes());
                        buff.write(json.getBytes());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());

                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
