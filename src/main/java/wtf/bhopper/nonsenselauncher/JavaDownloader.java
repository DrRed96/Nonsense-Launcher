package wtf.bhopper.nonsenselauncher;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaDownloader {

    private final File nonsenseDir;

    public JavaDownloader(File nonsenseDir) {
        this.nonsenseDir = nonsenseDir;
    }

    public void run() throws Exception {

        Downloads downloads = getJavaVersion(this.nonsenseDir);

        File check = new File(nonsenseDir, downloads.java);
        if (check.exists() && !check.isDirectory()) {
            return;
        }

        File downloadFile = new File(nonsenseDir, "java_download");
        downloadFile(downloads.url, downloadFile);

        if (downloads == Downloads.WINDOWS_X64) {
            unzip(downloadFile, nonsenseDir);
        } else {
            untar(downloadFile, nonsenseDir);
        }

        for (File file : nonsenseDir.listFiles()) {
            if (file.isDirectory() && file.getName().contains("jdk")) {
                file.renameTo(new File(nonsenseDir, "java"));
            }
        }

        downloadFile.delete();
        saveVersion(downloads, nonsenseDir);
    }

    public static void downloadFile(URL url, File output) throws IOException {
        System.out.print("\rDownloading: " + url + " 0%");
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
                        System.out.print("\rDownloading: " + url + " " + progress + "%");
                    }
                }
                System.out.println(", Done.");
            }
        }
    }

    public static void unzip(File zipFilePath, File dir) throws IOException {

        byte[] buffer = new byte[0x400];

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath.toPath()))) {
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(dir, fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();

                zis.closeEntry();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
        }
    }

    public static void untar(File zipFilePath, File dir) throws IOException {

        byte[] buffer = new byte[0x400];

        try (TarArchiveInputStream tar = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(Files.newInputStream(zipFilePath.toPath()))))) {
            ArchiveEntry entry = tar.getNextEntry();

            while (entry != null) {
                String fileName = entry.getName();
                File newFile = new File(dir, fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = tar.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                entry = tar.getNextEntry();
            }
        }

    }

    public static void saveVersion(Downloads downloads, File dir) {
        File file = new File(dir, "java.dat");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(downloads.toString());
        } catch (IOException ignored) {
        }
    }

    public static Downloads getJavaVersion(File dir) {
        Downloads savedVersion = getSavedVersion(dir);
        if (savedVersion != null) {
            return savedVersion;
        }

        int i = 1;
        for (Downloads download : Downloads.values()) {
            System.out.println(i + ". " + download.name);
            i++;
        }

        Scanner scanner = new Scanner(System.in);
        return getJavaVersionInput(scanner);
    }

    public static Downloads getSavedVersion(File dir) {
        File file = new File(dir, "java.dat");
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                return Downloads.valueOf(data);
            }
        } catch (IOException | IllegalArgumentException ignored) {
        }

        return null;
    }

    public static Downloads getJavaVersionInput(Scanner scanner) {
        System.out.print("Select a Java version (1-5): ");
        int v = scanner.nextInt();
        if (v < 1 || v > 5) {
            System.out.println("Invalid version: " + v);
            return getJavaVersionInput(scanner);
        }

        return Downloads.values()[v - 1];
    }

    public enum Downloads {
        LINUX_X64("Linux x64", "java/bin/java", "https://download.oracle.com/graalvm/23/latest/graalvm-jdk-23_linux-x64_bin.tar.gz"),
        LINUX_AARCH("Linux AArch64", "java/bin/java", "https://download.oracle.com/graalvm/23/latest/graalvm-jdk-23_linux-aarch64_bin.tar.gz"),
        MACOS_X64("macOS x64", "java/Contents/Home/bin/java", "https://download.oracle.com/graalvm/23/latest/graalvm-jdk-23_macos-x64_bin.tar.gz"),
        MACOS_ARRCH("macOS M1/AArch64", "java/Contents/Home/bin/java", "https://download.oracle.com/graalvm/23/latest/graalvm-jdk-23_macos-aarch64_bin.tar.gz"),
        WINDOWS_X64("Windows x64", "java/bin/java.exe", "https://download.oracle.com/graalvm/23/latest/graalvm-jdk-23_windows-x64_bin.zip");

        public final String name;
        public final String java;
        public final URL url;

        Downloads(String name, String java, String url) {
            try {
                this.name = name;
                this.java = java;
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
