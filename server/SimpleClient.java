import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SimpleClient {
    public static void main(String[] args) {
        final String host  =  "localhost";
        final int port = 8080;

        try (
            Socket socket = new Socket(host,port);
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
            InputStreamReader reader = new InputStreamReader(socket.getInputStream(),"UTF-8");
            ) {

            writer.write("Hello Server!\n");
            writer.flush();

            new Thread(()->{
                try {
                    
                    char[] buffer = new char[128];
                    while (true) {
                        int read = reader.read(buffer);
                        if (read == -1) {
                            break;
                        }
                        System.out.println("Server says: " + new String(buffer,0,read));
                        
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if(input.equals("exit")) {
                    break;
                }
                writer.write(input + "\n");
                writer.flush();

            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
