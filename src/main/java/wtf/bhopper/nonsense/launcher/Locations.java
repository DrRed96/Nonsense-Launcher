package wtf.bhopper.nonsense.launcher;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Locations {

    public static boolean checkOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }

    public static Path getDataPath() {
        return Paths.get(System.getenv("appdata"), "nonsense");
    }

    public static Path getMinecraftPath() {
        return Paths.get(System.getenv("appdata"), ".minecraft");
    }

}
