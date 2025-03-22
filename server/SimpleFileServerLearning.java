import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleFileServerLearning {

    final static int PORT = 8080;
    final static String FILE_PATH = "soda.mp4";

    public static void main(String[] args) {
        new SimpleFileServerLearning().start();
    }

    public void start() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket client = server.accept();
                System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    OutputStream out = client.getOutputStream();) {

                File file = new File(FILE_PATH);
                long contentLength = file.length();
                String firstLine = reader.readLine();
                if (firstLine.contains("HEAD")) {
                    out.write("HTTP/1.1 200 OK\r\n".getBytes());
                    out.write(("Content-Length: " + contentLength).getBytes());
                    out.write("\r\n".getBytes());
                    return;
                }

                long startBytes = 0;
                long endBytes = contentLength-1;

                boolean isPartial = false;
                String line;

                while (true) {
                    line = reader.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                    if (line.contains("Range")) {
                        isPartial = true;
                        startBytes = Long.parseLong(line.split("=")[1].split("-")[0]);
                        endBytes = Long.parseLong(line.split("=")[1].split("-")[1]);
                    }
                }

                out.write(("HTTP/1.1 " + (isPartial ? "206 Partial Content" : "200 OK") + "\r\n").getBytes());
                out.write("Content-Type: application/octet-stream\r\n".getBytes());
                out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
                if (isPartial) {
                    out.write(
                            ("Content-Range: bytes " + startBytes + "-" + endBytes + "/" + contentLength + "\r\n").getBytes());
                }
                out.write(("Content-Disposition: attachment; filename=\"" + new File(FILE_PATH).getName() + "\"\r\n")
                        .getBytes());
                out.write("\r\n".getBytes()); // End of headers

                try (RandomAccessFile inputFile = new RandomAccessFile(file, "r")) {
                    inputFile.seek(startBytes);
                    byte[] buffer = new byte[1024];
                    int read;
                    long bytesLeft = endBytes - startBytes + 1;
                    while (bytesLeft > 0 && (read = inputFile.read(buffer, 0, (int) Math.min(1024, bytesLeft))) != -1) {
                        out.write(buffer, 0, read);
                        bytesLeft -= read;
                        Thread.sleep(50);
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

}
