import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.*;

public class MultiThreadedDownloader {
    private static final int NUM_THREADS = 4; // Number of threads to use

    public static void main(String[] args) {
        String fileURL = "https://file-examples.com/storage/fe11f9541a67d9f2f9b2038/2017/04/file_example_MP4_640_3MG.mp4"; // Replace with your file URL
        String savePath = "sample.mp4"; // Output file path

        try {
            new MultiThreadedDownloader().downloadFile(fileURL, savePath, NUM_THREADS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(String fileURL, String savePath, int numThreads) throws Exception {
        URI uri = new URI(fileURL);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("HEAD"); // Get only the file info
        int fileSize = conn.getContentLength(); // Get file size

        if (fileSize <= 0) {
            throw new IOException("Failed to get file size.");
        }

        System.out.println("File size: " + fileSize / 1024 + " KB");
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int partSize = fileSize / numThreads; // Divide file into parts

        RandomAccessFile outputFile = new RandomAccessFile(savePath, "rw");
        outputFile.setLength(fileSize); // Set file size to avoid issues
        outputFile.close();

        for (int i = 0; i < numThreads; i++) {
            int startByte = i * partSize;
            int endByte = (i == numThreads - 1) ? fileSize - 1 : (startByte + partSize - 1);

            executor.execute(new DownloadTask(fileURL, savePath, startByte, endByte, i + 1));
        }

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println("Download complete!");
    }

    static class DownloadTask implements Runnable {
        private String fileURL, savePath;
        private int startByte, endByte, threadNumber;

        public DownloadTask(String fileURL, String savePath, int startByte, int endByte, int threadNumber) {
            this.fileURL = fileURL;
            this.savePath = savePath;
            this.startByte = startByte;
            this.endByte = endByte;
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URI(fileURL).toURL().openConnection();
                conn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                conn.connect();

                try (InputStream input = conn.getInputStream();
                     RandomAccessFile outputFile = new RandomAccessFile(savePath, "rw")) {
                    
                    outputFile.seek(startByte); // Move to correct position

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        outputFile.write(buffer, 0, bytesRead);
                    }
                }

                System.out.println("Thread " + threadNumber + " finished downloading.");
            } catch (IOException e) {
                System.err.println("Thread " + threadNumber + " error: " + e.getMessage());
            }
        }
    }
}
