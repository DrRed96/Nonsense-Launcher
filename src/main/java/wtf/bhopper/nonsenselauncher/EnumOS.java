package wtf.bhopper.nonsenselauncher;

import java.io.File;

public enum EnumOS {
    WINDOWS(new File(System.getenv("APPDATA"))),
    OSX(new File(System.getProperty("user.home"), "Library/Application Support")),
    LINUX(new File(System.getProperty("user.home"))),
    UNKNOWN(new File(System.getProperty("user.home")));

    public final File userFolder;

    EnumOS(File userFolder) {
        this.userFolder = userFolder;
    }

    public File getMinecraftFolder() {
        if (this == OSX) {
            return new File(this.userFolder, "minecraft");
        }

        return new File(this.userFolder, ".minecraft");
    }

    public static EnumOS getOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        }

        if (osName.contains("mac")) {
            return OSX;
        }

        if (osName.contains("linux") || osName.contains("unix")) {
            return LINUX;
        }

        return UNKNOWN;
    }
}
