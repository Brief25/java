package securevault.crypto;

import securevault.util.ProgressBar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class AESGCMFileDecryptor {

    private static final int TAG_LENGTH = 128;

    public static void decryptFile(File inputFile, File outputFile, char[] password) throws Exception {
        try (FileInputStream fis = new FileInputStream(inputFile);
             DataInputStream dis = new DataInputStream(fis);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            // ---- READ FILE HEADER ----
            int saltLength = dis.readInt();
            byte[] salt = new byte[saltLength];
            dis.readFully(salt);

            int ivLength = dis.readInt();
            byte[] iv = new byte[ivLength];
            dis.readFully(iv);

            // ---- DERIVE KEY ----
            byte[] key = KeyDerivation.deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // ---- DECRYPT DATA WITH PROGRESS ----
            long totalBytes = inputFile.length() - (4 + saltLength + 4 + ivLength); // minus header
            long processedBytes = 0;

            byte[] buffer = new byte[8192];
            int bytesRead;

            try (CipherInputStream cis = new CipherInputStream(dis, cipher)) {
                ProgressBar.update("Decrypting", 0, totalBytes);

                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    processedBytes += bytesRead;
                    ProgressBar.update("Decrypting", processedBytes, totalBytes);
                }

                ProgressBar.forceComplete("Decrypting");
            }
        }
    }
}
