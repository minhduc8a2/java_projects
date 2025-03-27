package com.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestHandler implements Runnable {
    private final static String DIRECTORY_PATH = "D:/Documents/Test";
    private final static String UPLOAD_DIR = "D:/Documents/Test/upload";
    private final static int BUFFER_SIZE = 1024 * 4;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static int connectionCount = 0;
    private Socket socket;
    private Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private static final Object LOCK = new Object();

    public RequestHandler(Socket socket) {
        this.socket = socket;
        synchronized (LOCK) {
            connectionCount++;
            System.out.println("Connection count: " + connectionCount);
        }
    }

    public void run() {
        try (
                InputStream inputStream = socket.getInputStream();
                BufferedReader request = new BufferedReader(new InputStreamReader(inputStream));
                BufferedOutputStream response = new BufferedOutputStream(socket.getOutputStream());) {
            String requestLine = request.readLine();
            if (requestLine == null)
                return;
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                return;
            }
            String method = requestLine.split(" ")[0];
            switch (method) {
                case "GET" -> {
                    Path baseDir = Paths.get(DIRECTORY_PATH);
                    String requestedFilePath = requestLine.split(" ")[1];
                    requestedFilePath = URLDecoder.decode(requestedFilePath, "UTF-8");
                    System.out.println("original requestedPath: " + requestedFilePath);

                    if (requestedFilePath.startsWith("/")) {
                        requestedFilePath = requestedFilePath.substring(1);

                    }
                    System.out.println("requestedPath: " + requestedFilePath);
                    Path filePath = baseDir.resolve(requestedFilePath);
                    System.out.println("filePath: " + filePath);
                    File requestFile = filePath.toFile();
                    if (requestFile.exists()) {
                        if (requestFile.isDirectory()) {
                            sendDirectoryListing(response, requestFile, requestedFilePath);
                        } else {
                            sendFile(response, requestFile);
                        }
                    }
                }

                case "POST" -> {
                    if (URLDecoder.decode(requestParts[1], StandardCharsets.UTF_8).equals("/upload")) {
                        handleFileUpload(request, inputStream, response);
                    } else {
                        sendError(response, 405, "Method Not Allowed");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Close the connection
            } catch (IOException e) {
                logger.warning("Error closing socket: " + e.getMessage());
            }

            // Decrease connection count safely
            synchronized (LOCK) {
                connectionCount--;
                System.out.println("Connection closed. Active connections: " + connectionCount);
            }
        }
    }

    public void sendDirectoryListing(BufferedOutputStream response, File requestFile, String requestFilePath) {
        try {
            File[] files = requestFile.listFiles();
            List<FileMeta> fileMetas = new ArrayList<>();
            for (File file : files) {
                Path basePath = Paths.get(requestFilePath);
                String relativePath = basePath.resolve(file.getName()).toString();
                String encodedPath = URLEncoder.encode(relativePath, StandardCharsets.UTF_8);
                System.out.println("encodedPath: " + encodedPath);
                fileMetas.add(new FileMeta(file.getName(), encodedPath, file.length(), file.isDirectory()));

            }
            String json = objectMapper.writeValueAsString(fileMetas);
            response.write("HTTP/1.1 200 OK\r\n".getBytes());
            response.write("Content-Type: application/json\r\n".getBytes());
            response.write(("Content-Length: " + json.getBytes().length + "\r\n").getBytes());
            response.write("\r\n".getBytes());
            logger.info(json);
            response.write(json.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(BufferedOutputStream response, File file) throws IOException {
        if (!file.exists()) {
            logger.info("File not found: " + file.getName());
            sendError(response, 404, "File not found");
            return;
        }

        logger.info("Sending file: " + file.getName());
        response.write("HTTP/1.1 200 OK\r\n".getBytes());

        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            response.write("Content-Type: application/octet-stream\r\n".getBytes());
        } else {

            response.write(("Content-Type: " + contentType + "\r\n").getBytes());
        }

        response.write(("Content-Length: " + file.length() + "\r\n").getBytes());
        response.write("\r\n".getBytes());
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024 * 4];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.write(buffer, 0, bytesRead);
            }
            response.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendError(OutputStream output, int statusCode, String message) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "\r\n" +
                message;
        output.write(response.getBytes());
    }

    private void handleFileUpload(BufferedReader request, InputStream inputStream, BufferedOutputStream response) {
        logger.info("Uploading file reached");

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists())
                uploadDir.mkdirs();

            String line;
            String boundary = null;

            // Read headers
            while ((line = request.readLine()) != null && !line.isEmpty()) {
                logger.info("Header Line: " + line);
                if (line.startsWith("Content-Type: multipart/form-data;")) {
                    int boundaryIndex = line.indexOf("boundary=");
                    if (boundaryIndex != -1) {
                        boundary = "--" + line.substring(boundaryIndex + 9).trim();
                    }
                }
            }

            if (boundary == null) {
                sendError(response, 400, "Missing boundary");
                return;
            }
            // boundary "--webkit"
            line = request.readLine();
            // Read Content-Disposition header
            line = request.readLine();

            if (line == null || !line.contains("filename=")) {
                sendError(response, 400, "Missing filename");
                return;
            }

            String filename = extractFilename(line);
            File file = new File(uploadDir, filename);

            // Skip Content-Type and empty line
            request.readLine(); // Content-Type
            request.readLine(); // Empty line

            logger.info("Uploading file: " + file.getName());

            // Switch to InputStream for file content
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                boolean boundaryReached = false;

                while ((bytesRead = inputStream.read(buffer)) != -1) {

                    fos.write(buffer, 0, bytesRead); // Write only file content

                }

             
                logger.info("File uploaded successfully: " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.warning("Error writing file: " + e.getMessage());
                sendError(response, 500, "File writing error");
                return;
            }

            sendResponse(response, 200, "File uploaded successfully");

        } catch (IOException e) {
            logger.warning("Error processing upload: " + e.getMessage());
            try {
                sendError(response, 500, "Internal server error");
            } catch (IOException ignored) {
            }
        }
    }

    private void sendResponse(OutputStream output, int statusCode, String message) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + message.length() + "\r\n" +
                "\r\n" +
                message;
        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private String extractFilename(String contentDisposition) {
        int startIndex = contentDisposition.indexOf("filename=\"") + 10;
        int endIndex = contentDisposition.indexOf("\"", startIndex);
        return contentDisposition.substring(startIndex, endIndex);
    }
}
