package wtf.bhopper.nonsense.launcher;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum EnumOS {

    LINUX,
    WINDOWS,
    OSX,
    UNKNOWN;

    public static EnumOS getOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win") ? WINDOWS
                : (osName.contains("mac") ? OSX
                : (osName.contains("linux") ? LINUX
                : (osName.contains("unix") ? LINUX
                : UNKNOWN)));
    }

    public static Path getDataPath() {
        switch (getOs())  {

            case WINDOWS:
                return Paths.get(System.getenv("appdata"), "nonsense");

            case OSX:
                return Paths.get("~/Library/Application Support/nonsense");

            case LINUX:
                return Paths.get("~/nonsense");

            default:
                throw new RuntimeException("Unknown operating system");
        }
    }

    public static Path getMinecraftPath() {
        switch (getOs())  {

            case WINDOWS:
                return Paths.get(System.getenv("appdata"), ".minecraft");

            case OSX:
                return Paths.get("~/Library/Application Support/minecraft");

            case LINUX:
                return Paths.get("~/.minecraft");

            default:
                throw new RuntimeException("Unknown operating system");
        }
    }

    public static String getNativesZip() {
        switch (getOs())  {

            case WINDOWS:
                return "windows_natives.zip";

            case OSX:
                return "osx_natives.zip";

            case LINUX:
                return "linux_natives.zip";

            default:
                throw new RuntimeException("Unknown operating system");
        }
    }

}
