package wtf.bhopper.nonsense.launcher;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class Http {

    public HttpURLConnection connection;

    public Http(String url) throws Exception {
        this.connection = (HttpURLConnection)(new URL(url)).openConnection();
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
    }

    public Http header(String key, String value) {
        this.connection.setRequestProperty(key, value);
        return this;
    }

    public Http postRaw(String s) throws IOException {
        this.connection.setRequestMethod("POST");
        byte[] out = s.getBytes(StandardCharsets.UTF_8);
        this.connection.connect();
        OutputStream os = this.connection.getOutputStream();
        os.write(out);
        os.flush();
        os.close();
        return this;
    }

    public Http postJson(Object object) throws IOException {
        header("Content-Type", "application/json");
        postRaw(new Gson().toJson(object));
        return this;
    }

    public Http postUrlEncoded(Map<Object, Object> map) throws IOException {
        header("Content-Type", "application/x-www-form-urlencoded");
        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        postRaw(sj.toString());
        return this;
    }

    public Http get() throws ProtocolException {
        this.connection.setRequestMethod("GET");
        return this;
    }

    public Http get(Map<Object, Object> map) throws ProtocolException {
        this.connection.setRequestMethod("GET");
        return this;

    }

    public int status() throws IOException {
        return this.connection.getResponseCode();
    }

    public String body() throws IOException {
        StringBuilder sb = new StringBuilder();
        Reader r = new InputStreamReader(this.connection.getInputStream(), StandardCharsets.UTF_8);
        int i;
        while ((i = r.read()) >= 0) {
            sb.append((char) i);
        }
        r.close();
        return sb.toString();
    }


}
