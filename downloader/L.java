import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class L {

    public static void main(String[] args) {
        try (
            BufferedReader reader = new BufferedReader(new FileReader("data.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            
            ) {
           
                int charData ;
                while ((charData = reader.read()) !=-1) {
                    writer.write( charData);
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
