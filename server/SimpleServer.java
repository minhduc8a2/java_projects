import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
public class SimpleServer {
    private static final Logger LOGGER = Logger.getLogger("SimpleServer_LOGGER");
    public static void main(String[] args) {
        final int PORT  =  8080;

        try (ServerSocket server = new ServerSocket(PORT)
        ) {

            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.info("New client connected: " + clientSocket.getInetAddress());
                       try (InputStreamReader reader = new InputStreamReader(clientSocket.getInputStream());
                        OutputStreamWriter writer = new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8")
                       ) {
                        writer.write("Hello Client!\n");
                            writer.flush();
                        char[] buffer = new char[128];
                        while (true) {
                            int read = reader.read(buffer);
                            if (read == -1) {
                                break;
                            }
                            System.out.println("Client says: " + new String(buffer,0,read));
                            writer.write("I got your message: " + new String(buffer,0,read) + "\n");
                            writer.flush();
                            
                        }
                       } catch (Exception e) {
                        // TODO: handle exception
                       }

                    }
                }).start();
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
