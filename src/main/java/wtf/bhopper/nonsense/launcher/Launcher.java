package wtf.bhopper.nonsense.launcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Launcher {

    public static final String DOWNLOAD_URL_BASE = "https://bhopper.wtf/nonsense/download/";

    public final Path dataPath;

    public Launcher() throws Exception {
        this.dataPath = EnumOS.getDataPath();
        this.dataPath.toFile().mkdirs();
        this.copyAssets();
        this.downloadFile("client.jar");
        this.downloadFile("libs.jar");
        this.downloadNatives(EnumOS.getNativesZip());
        this.launch();
    }

    public void downloadFile(String name) throws Exception {

        System.out.println("Downloading: " + name);

        File file = dataPath.resolve(name).toFile();
        boolean doDownload = false;

        if (!file.exists() || !file.isFile()) {
            doDownload = true;
        }

        if (!doDownload) {

            String fileHash = FileUtil.byteArrayToHex(FileUtil.sha1(file));

            Http http = new Http(DOWNLOAD_URL_BASE + "checksum")
                    .header("User-Agent", "curl")
                    .header("file", name)
                    .get();

            if (http.status() != 200) {
                throw new RuntimeException("Invalid status: " + http.status() + ", " + http.body());
            }

            if (!fileHash.equalsIgnoreCase(http.body())) {
                doDownload = true;
            }
        }

        if (doDownload) {
            new FileDownloader(new URL(DOWNLOAD_URL_BASE + name), file).download();
        }

    }

    public void downloadNatives(String name) throws Exception {

        System.out.println("Downloading: " + name);

        File hashFile = dataPath.resolve("natives.sha1").toFile();
        boolean doDownload = false;

        if (!hashFile.exists() || !hashFile.isFile()) {
            doDownload = true;
        }

        if (!doDownload) {

            String fileHash = FileUtil.readFile(hashFile, StandardCharsets.UTF_8);

            Http http = new Http(DOWNLOAD_URL_BASE + "checksum")
                    .header("User-Agent", "curl")
                    .header("file", name)
                    .get();

            if (http.status() != 200) {
                throw new RuntimeException("Invalid status: " + http.status() + ", " + http.body());
            }

            if (!fileHash.equalsIgnoreCase(http.body())) {
                doDownload = true;
            }
        }

        if (doDownload) {
            File nativesDir = dataPath.resolve("natives").toFile();
            String[] toDelete = nativesDir.list();
            if (toDelete != null) {
                for (String file : toDelete) {
                    new File(nativesDir.getPath(), file).delete();
                }
            }
            nativesDir.delete();

            File zipFile = dataPath.resolve("natives.zip").toFile();
            new FileDownloader(new URL(DOWNLOAD_URL_BASE + name), zipFile).download();

            // Save the file checksum
            FileWriter writer = new FileWriter(hashFile);
            writer.write(FileUtil.byteArrayToHex(FileUtil.sha1(zipFile)));
            writer.close();

            FileUtil.unzipArchive(zipFile, nativesDir);
            Files.delete(zipFile.toPath());
        }
    }

    public void copyAssets() throws IOException {

        System.out.println("Copying assets...");

        Path assetsDir = this.dataPath.resolve("assets");
        Path mcAssetsDir = EnumOS.getMinecraftPath().resolve("assets");
        File indexes = assetsDir.resolve("indexes").toFile();
        File logConfigs = assetsDir.resolve("log_configs").toFile();
        File objects = assetsDir.resolve("objects").toFile();

        if (!indexes.exists()) {
            indexes.mkdirs();
            FileUtil.copyDirectory(mcAssetsDir.resolve("indexes").toFile(), indexes);
        }
        if (!logConfigs.exists()) {
            logConfigs.mkdirs();
            FileUtil.copyDirectory(mcAssetsDir.resolve("log_configs").toFile(), logConfigs);
        }
        if (!objects.exists()) {
            objects.mkdirs();
            FileUtil.copyDirectory(mcAssetsDir.resolve("objects").toFile(), objects);
        }
    }

    public void launch() throws IOException {

        String javaHome = System.getProperty("java.home");
        if (!javaHome.contains("1.8")) {
            System.out.println();
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("WARNING: We have detected that you are not using Java 1.8.");
            System.out.println("make sure you are using Java 1.8 or the client may not launch properly!");
            System.out.println("You can download Java 1.8 at: https://www.java.com/en/download/manual.jsp");
            System.out.println();
            if (EnumOS.getOs() == EnumOS.WINDOWS) {
            System.out.println("If you are still having problems after installing Java 1.8 try running");
            System.out.println("launchfixed.bat");
            System.out.println();
            }
            System.out.println("For support message @calculushvh on Discord.");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println();
        }
        String javaPath = Paths.get(System.getProperty("java.home"), EnumOS.getOs() == EnumOS.WINDOWS ? "bin/java.exe" : "bin/java").toFile().getAbsolutePath();
        String[] command = new String[]{
                "\"" + javaPath + "\"",
                "-Xms1024M",
                "-Xmx4G",
                "-Djava.library.path=natives",
                "-cp", "libs.jar;client.jar",
                "net.minecraft.client.main.Main",
                "--version", "Nonsense",
                "--accessToken", "0",
                "--gameDir", "\"" + EnumOS.getMinecraftPath().toFile().getAbsolutePath() + "\"",
                "--assetsDir", "assets",
                "--assetIndex", "1.8",
                "--userProperties", "{}"
        };

        System.out.println("Launching: " + String.join(" ", command));
        Runtime.getRuntime().exec(command, null, this.dataPath.toFile());
    }

    public static void main(String[] args) {
        try {
            new Launcher();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
