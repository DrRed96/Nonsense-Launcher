package wtf.bhopper.nonsenselauncher;

import java.io.*;

public class Main {

    public static void main(String[] args) {

        File nonsenseFolder = new File(EnumOS.getOs().userFolder, "nonsense");
        nonsenseFolder.mkdirs();

        try {
            new JavaDownloader(nonsenseFolder).run();
            new NonsenseDownloader(nonsenseFolder).run();
        } catch (Exception e) {
            System.err.println("An exception occurred while downloading. \nIf this continues report it to @calculushvh on Discord.");
            e.printStackTrace();
            return;
        }

        JavaDownloader.Downloads downloads = JavaDownloader.getSavedVersion(nonsenseFolder);

        if (downloads == null) {
            throw new IllegalStateException("Failed to read java.dat file!");
        }

        String javaPath = new File(nonsenseFolder, downloads.java).getPath();

        EnumOS os = EnumOS.getOs();
        File mcFolder = os.getMinecraftFolder();
        File assetFolder = new File(mcFolder, "assets");

        String[] command = {
                "\"" + javaPath + "\"",
                "-Xms1024M",
                "-Xmx4G",
                "-jar", "client.jar",
                "net.minecraft.client.main.Main",
                "--version", "Nonsense",
                "--accessToken", "0",
                "--username", "Nonsense",
                "--gameDir", "\"" + mcFolder + "\"",
                "--assetsDir", "\"" + assetFolder + "\"",
                "--assetIndex", "1.8",
                "--userProperties", "{}"
        };

        System.out.println("Launching...");
        System.out.println(String.join(" ", command));

        try {
            Runtime.getRuntime().exec(command, null, nonsenseFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
