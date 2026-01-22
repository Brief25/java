package securevault.cli;

import securevault.crypto.AESGCMFileEncryptor;
import securevault.crypto.AESGCMFileDecryptor;
import securevault.crypto.KeyDerivation;
import securevault.crypto.PasswordReader;
import securevault.util.ProgressBar;

import java.io.File;
import java.util.Scanner;

public class CommandParser {

    public static void parse(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "encrypt":
                handleEncrypt(args);
                break;

            case "decrypt":
                handleDecrypt(args);
                break;

            case "--help":
            case "-h":
            case "help":
                printHelp();
                break;

            default:
                System.err.println("❌ Unknown command: " + command);
                printHelp();
        }
    }

    // ---------------- ENCRYPTION ----------------
    private static void handleEncrypt(String[] args) {
        try {
            // 1️⃣ Get input/output from args
            String inputPath = getArgValue(args, "--input");
            String outputPath = getArgValue(args, "--output");

            if (inputPath == null) {
                System.err.println("❌ Error: --input is required");
                return;
            }

            File inputFile = new File(inputPath);
            if (!inputFile.exists()) {
                System.err.println("❌ Input file/folder not found");
                return;
            }

            // 2️⃣ Read password
            char[] password = PasswordReader.readPassword("Enter encryption password: ");
            char[] confirm = PasswordReader.readPassword("Confirm password: ");
            if (!java.util.Arrays.equals(password, confirm)) {
                System.err.println("❌ Passwords do not match");
                return;
            }

            // 3️⃣ Derive key & salt
            byte[] salt = KeyDerivation.generateSalt();
            byte[] key = KeyDerivation.deriveKey(password, salt);

            // 4️⃣ Encrypt file or folder
            if (inputFile.isDirectory()) {
                File outDir = (outputPath != null) ? new File(outputPath)
                        : new File(inputFile.getParent(), inputFile.getName() + "_enc");

                encryptFolder(inputFile, outDir, key, salt);
                System.out.println("\n✅ Folder encrypted successfully: " + outDir.getAbsolutePath());
            } else {
                File outputFile = (outputPath != null) ? new File(outputPath)
                        : new File(inputFile.getAbsolutePath() + ".enc");

                AESGCMFileEncryptor.encryptFile(inputFile, outputFile, key, salt);
                System.out.println("\n✅ File encrypted successfully: " + outputFile.getAbsolutePath());
            }

            // 5️⃣ Ask about deleting original AFTER encryption
            Scanner sc = new Scanner(System.in);
            System.out.print("Do you want to delete original file/folder after encryption? (yes/no): ");
            boolean deleteOriginal = sc.nextLine().trim().equalsIgnoreCase("yes");

            if (deleteOriginal) {
                deleteFileOrFolder(inputFile);
                System.out.println("🗑 Original file/folder deleted.");
            } else {
                System.out.println("ℹ Original file/folder kept.");
            }

        } catch (Exception e) {
            System.err.println("❌ Encryption failed: " + e.getMessage());
        }
    }

    // ---------------- DECRYPTION ----------------
    private static void handleDecrypt(String[] args) {
        try {
            // Get input/output paths from args
            String inputPath = getArgValue(args, "--input");
            String outputPath = getArgValue(args, "--output");

            if (inputPath == null) {
                System.err.println("❌ Error: --input is required");
                return;
            }

            File encFile = new File(inputPath);
            if (!encFile.exists()) {
                System.err.println("❌ Encrypted file/folder not found");
                return;
            }

            File outputFile;
            if (outputPath == null) {
                if (encFile.isDirectory()) {
                    outputFile = new File(encFile.getParent(), encFile.getName().replace("_enc", ""));
                } else {
                    outputFile = new File(encFile.getParent(), encFile.getName().replace(".enc", ""));
                }
            } else {
                outputFile = new File(outputPath);
            }

            // Read password
            char[] password = PasswordReader.readPassword("Enter decryption password: ");

            // Decrypt
            if (encFile.isDirectory()) {
                decryptFolder(encFile, outputFile, password);
                System.out.println("\n✅ Folder decrypted successfully: " + outputFile.getAbsolutePath());
            } else {
                AESGCMFileDecryptor.decryptFile(encFile, outputFile, password);
                System.out.println("\n✅ File decrypted successfully: " + outputFile.getAbsolutePath());
            }

            // Optional deletion
            Scanner sc = new Scanner(System.in);
            System.out.print("Do you want to delete original file/folder after decryption? (yes/no): ");
            boolean deleteOriginal = sc.nextLine().trim().equalsIgnoreCase("yes");

            if (deleteOriginal) {
                deleteFileOrFolder(encFile);
                System.out.println("🗑 Original file/folder deleted.");
            } else {
                System.out.println("ℹ Original file/folder kept.");
            }

        } catch (Exception e) {
            System.err.println("❌ Decryption failed (wrong password or file modified)");
        }
    }

    // ---------------- HELPER: DELETE ----------------
    private static void deleteFileOrFolder(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteFileOrFolder(child);
            }
        }
        file.delete();
    }

    // ---------------- HELPER: FOLDER ENCRYPTION ----------------
    private static void encryptFolder(File inputDir, File outputDir, byte[] key, byte[] salt) throws Exception {
        if (!outputDir.exists()) outputDir.mkdirs();

        File[] files = inputDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                encryptFolder(file, new File(outputDir, file.getName()), key, salt);
            } else {
                File outFile = new File(outputDir, file.getName() + ".enc");
                AESGCMFileEncryptor.encryptFile(file, outFile, key, salt);
            }
        }
    }

    // ---------------- HELPER: FOLDER DECRYPTION ----------------
    private static void decryptFolder(File inputDir, File outputDir, char[] password) throws Exception {
        if (!outputDir.exists()) outputDir.mkdirs();

        File[] files = inputDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                decryptFolder(file, new File(outputDir, file.getName().replace("_enc", "")), password);
            } else {
                String outName = file.getName().replace(".enc", "");
                File outFile = new File(outputDir, outName);
                AESGCMFileDecryptor.decryptFile(file, outFile, password);
            }
        }
    }

    // ---------------- HELPER: GET ARG VALUE ----------------
    private static String getArgValue(String[] args, String key) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase(key)) {
                return args[i + 1];
            }
        }
        return null;
    }

    // ---------------- HELP ----------------
   public static void printHelp() {
    System.out.println("""
══════════════════════════════════════════════════════════
🔐 SecureVault-CLI — Secure File & Folder Encryption Tool
Version: 1.0 (Final Year Project - 2026)
══════════════════════════════════════════════════════════

📌 WHAT THIS TOOL DOES
• Encrypts files or folders securely using AES-256 (GCM)
• Decrypts encrypted files or folders
• Shows live progress bar during encryption/decryption
• Optionally deletes original data after completion

──────────────────────────────────────────────────────────
📌 BASIC COMMAND FORMAT

  securevault <command> --input <path> [--output <path>]

──────────────────────────────────────────────────────────
📌 AVAILABLE COMMANDS

  encrypt     Encrypt a file or a folder
  decrypt     Decrypt a file or a folder
  help        Show this help menu

──────────────────────────────────────────────────────────
📌 REQUIRED OPTIONS

  --input     Path of file OR folder to encrypt/decrypt

──────────────────────────────────────────────────────────
📌 OPTIONAL OPTIONS

  --output    Output path (optional)
              If not provided:
              • Encrypted file → .enc extension added
              • Encrypted folder → _enc folder created

──────────────────────────────────────────────────────────
📌 EXAMPLES

🔹 Encrypt a single file:
  securevault encrypt --input notes.pdf

🔹 Encrypt a file with custom output:
  securevault encrypt --input notes.pdf --output notes.sec

🔹 Encrypt a folder:
  securevault encrypt --input MyFolder

🔹 Decrypt a file:
  securevault decrypt --input notes.pdf.enc

🔹 Decrypt a folder:
  securevault decrypt --input MyFolder_enc

──────────────────────────────────────────────────────────
📌 IMPORTANT NOTES

• Password confirmation is required during encryption
• Progress bar starts automatically (no Enter needed)
• You will be asked whether to delete original data
• Wrong password = decryption fails safely
• No data is sent over the internet (offline tool)

──────────────────────────────────────────────────────────
📌 HELP

  securevault help
  securevault --help
  securevault -h

══════════════════════════════════════════════════════════
""");
   }
}
