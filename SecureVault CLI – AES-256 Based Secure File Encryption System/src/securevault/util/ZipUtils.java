package securevault.util;

import java.io.*;
import java.util.zip.*;

public class ZipUtils {

    public static File zipFolder(File sourceDir) throws IOException {
        File zipFile = new File(sourceDir.getAbsolutePath() + ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zipRecursive(sourceDir, sourceDir.getName(), zos);
        }
        return zipFile;
    }

    private static void zipRecursive(File file, String entryName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                zipRecursive(child, entryName + "/" + child.getName(), zos);
            }
        } else {
            zos.putNextEntry(new ZipEntry(entryName));
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(zos);
            }
            zos.closeEntry();
        }
    }

    public static void unzip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) destDir.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        zis.transferTo(fos);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
