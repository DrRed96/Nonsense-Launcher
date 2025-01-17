package wtf.bhopper.nonsenselauncher;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NonsenseDownloader {

    public static final DownloadFile[] DOWNLOAD_FILES = {
            new DownloadFile("client.jar"),
    };

    private final File nonsenseDir;

    public NonsenseDownloader(File nonsenseDir) {
        this.nonsenseDir = nonsenseDir;
    }

    public void run() throws Exception {

        System.out.println("Checking for updates...");

        for (DownloadFile downloadFile : DOWNLOAD_FILES) {
            File file = new File(this.nonsenseDir, downloadFile.name);
            String fileHash = getFileHash(file);
            String hash = getTextFromUrl(downloadFile.hashUrl).trim();

            if (fileHash == null || !fileHash.equals(hash)) {
                downloadFile(downloadFile.downloadUrl, file);
            }
        }
    }

    public static String getTextFromUrl(URL url) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
        }
        return content.toString();
    }

    public static void downloadFile(URL url, File output) throws IOException {
        System.out.print("\rDownloading " + output.getName() + ": 0%");
        try (InputStream in = new BufferedInputStream(url.openStream())) {
            try (FileOutputStream out = new FileOutputStream(output)) {
                byte[] buffer = new byte[1024];
                long totalBytesRead = 0L;
                long fileSize = url.openConnection().getContentLengthLong();
                int progress = 0;
                int bytesRead;
                while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int newProgress = (int) (totalBytesRead * 100L / fileSize);
                    if (newProgress > progress) {
                        progress = newProgress;
                        System.out.print("\rDownloading " + output.getName() + ": " + progress + "%");
                    }
                }
                System.out.println(", Done.");
            }
        }
    }

    public static String getFileHash(File file) throws NoSuchAlgorithmException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (FileInputStream stream = new FileInputStream(file)) {
                byte[] byteArray = new byte[0x400];
                int bytesCount;
                while ((bytesCount = stream.read(byteArray)) != -1) {
                    digest.update(byteArray, 0, bytesCount);
                }
            }

            byte[] bytes = digest.digest();

            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (IOException e) {
            return null;
        }
    }

}
