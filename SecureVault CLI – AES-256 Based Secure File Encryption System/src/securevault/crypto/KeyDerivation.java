package securevault.crypto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class KeyDerivation {

    private static final int KEY_LENGTH = 256; // bits
    private static final int ITERATIONS = 310_000; // modern standard

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static byte[] deriveKey(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        SecretKeyFactory skf =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return skf.generateSecret(spec).getEncoded();
    }
}
