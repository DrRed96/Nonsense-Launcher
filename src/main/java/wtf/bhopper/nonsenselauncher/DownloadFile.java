package wtf.bhopper.nonsenselauncher;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloadFile {

    public final URL downloadUrl;
    public final URL hashUrl;
    public final String name;

    public DownloadFile(String name) {
        try {
            this.downloadUrl = new URL("https://github.com/DrRed96/Nonsense-Release/raw/refs/heads/main/" + name);
            this.hashUrl = new URL("https://github.com/DrRed96/Nonsense-Release/raw/refs/heads/main/" + name + ".sha256");
            this.name = name;
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

}
