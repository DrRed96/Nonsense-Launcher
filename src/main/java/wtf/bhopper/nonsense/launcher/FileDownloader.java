package wtf.bhopper.nonsense.launcher;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class FileDownloader {

    public final URL url;
    public final File file;

    public FileDownloader(URL url, File file) {
        this.url = url;
        this.file = file;
    }

    public void download() throws IOException, URISyntaxException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(this.url.toURI());
        httpGet.setHeader("User-Agent", "curl");
        httpClient.execute(httpGet, classicHttpResponse -> {
            int code = classicHttpResponse.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = classicHttpResponse.getEntity();
                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(this.file)) {
                        byte[] dataBuffer = new byte[0x400];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                    }

                }
                EntityUtils.consume(entity);
            }
            return classicHttpResponse;
        });
    }

}
