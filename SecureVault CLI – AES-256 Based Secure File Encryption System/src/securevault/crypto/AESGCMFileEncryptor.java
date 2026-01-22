package securevault.crypto;

import securevault.util.ProgressBar;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;

public class AESGCMFileEncryptor {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    public static void encryptFile(
            File inputFile,
            File outputFile,
            byte[] key,
            byte[] salt
    ) throws Exception {

        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

        long totalBytes = inputFile.length();
        long processedBytes = 0;

        try (
                FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(outputFile);
                DataOutputStream dos = new DataOutputStream(fos)
        ) {
            // ---- FILE HEADER ----
            dos.writeInt(salt.length);
            dos.write(salt);

            dos.writeInt(iv.length);
            dos.write(iv);

            // ---- INIT PROGRESS BAR ----
            ProgressBar.update("Encrypting", 0, totalBytes);

            // ---- ENCRYPT DATA ----
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encryptedChunk = cipher.update(buffer, 0, bytesRead);
                if (encryptedChunk != null) {
                    dos.write(encryptedChunk);
                    dos.flush(); // flush after each chunk for live progress
                }

                processedBytes += bytesRead;
                ProgressBar.update("Encrypting", processedBytes, totalBytes);
            }

            // ---- FINAL BLOCK ----
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                dos.write(finalBlock);
                dos.flush();
            }

            // ---- ENSURE PROGRESS BAR COMPLETE ----
            ProgressBar.forceComplete("Encrypting");
        }
    }
}
