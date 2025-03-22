import java.io.*;
import java.net.*;

public class SimpleFileServer {
    private static final int PORT = 8080;
    private static  String FILE_PATH = "soda.mp4";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            if (args.length > 0) {
                FILE_PATH = args[0];
            }
            System.out.println("🚀 Server started at http://localhost:" + PORT);
            System.out.println("📁 Serving file: " + FILE_PATH);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new FileHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class FileHandler implements Runnable {
        private Socket socket;

        public FileHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream output = socket.getOutputStream()) {

                // Read request line
                String requestLine = reader.readLine();
                if (requestLine == null || (!requestLine.startsWith("GET") && !requestLine.startsWith("HEAD"))) {
                    return;
                }

                // Read headers
                String rangeHeader = null;
                while (true) {
                    String line = reader.readLine();
                    if (line == null || line.isEmpty()) break;
                    if (line.startsWith("Range:")) {
                        rangeHeader = line;
                    }
                }

                File file = new File(FILE_PATH);
                if (!file.exists()) {
                    sendError(output, 404, "File Not Found");
                    return;
                }

                long fileLength = file.length();
                long start = 0, end = fileLength - 1;
                boolean isPartial = false;

                if (requestLine.startsWith("HEAD")) {
                    sendHeaders(output, 200, fileLength, false, start, end);
                    return;
                }

                // Handle Range Header
                if (rangeHeader != null) {
                    try {
                        isPartial = true;
                        String rangeValue = rangeHeader.split("=")[1];
                        String[] rangeParts = rangeValue.split("-");
                        start = Long.parseLong(rangeParts[0]);
                        if (rangeParts.length > 1 && !rangeParts[1].isEmpty()) {
                            end = Long.parseLong(rangeParts[1]);
                        }
                        if (start >= fileLength || end >= fileLength) {
                            sendError(output, 416, "Range Not Satisfiable");
                            return;
                        }
                    } catch (Exception e) {
                        sendError(output, 400, "Bad Request");
                        return;
                    }
                }

                long contentLength = end - start + 1;
                sendHeaders(output, isPartial ? 206 : 200, contentLength, isPartial, start, end);

                // Send file content
                if (requestLine.startsWith("GET")) {
                    sendFile(output, file, start, contentLength);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }

        private void sendHeaders(OutputStream output, int status, long contentLength, boolean isPartial, long start, long end) throws IOException {
            output.write(("HTTP/1.1 " + status + (isPartial ? " Partial Content" : " OK") + "\r\n").getBytes());
            output.write("Content-Type: video/mp4\r\n".getBytes());
            output.write(("Content-Length: " + contentLength + "\r\n").getBytes());
            if (isPartial) {
                output.write(("Content-Range: bytes " + start + "-" + end + "/" + new File(FILE_PATH).length() + "\r\n").getBytes());
            }
            output.write("Accept-Ranges: bytes\r\n".getBytes());
            output.write("\r\n".getBytes());
        }

        private void sendFile(OutputStream output, File file, long start, long contentLength) throws Exception {
            byte[] buffer = new byte[65536]; // 64 KB buffer
            try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                raf.seek(start);
                long bytesLeft = contentLength;
                int bytesRead;
                while (bytesLeft > 0 && (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, bytesLeft))) != -1) {
                    try {
                        output.write(buffer, 0, bytesRead);
                        output.flush();
                        Thread.sleep(20);
                    } catch (SocketException e) {
                        System.out.println("⚠️ Client disconnected while sending data.");
                        return;
                    }
                    bytesLeft -= bytesRead;
                }
            }
        }

        private void sendError(OutputStream output, int statusCode, String message) throws IOException {
            output.write(("HTTP/1.1 " + statusCode + " " + message + "\r\n").getBytes());
            output.write("\r\n".getBytes());
        }
    }
}
