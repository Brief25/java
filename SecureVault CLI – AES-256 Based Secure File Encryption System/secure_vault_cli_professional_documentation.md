# SecureVault CLI

## Secure File Encryption & Decryption Tool (2026 Edition)

---

## 1. Introduction

SecureVault CLI is a **professional-grade command-line file encryption tool** developed using modern cryptographic standards. It is designed to protect sensitive files such as documents, images, videos, and audio from unauthorized access.

In an era where **data breaches, ransomware, and privacy violations** are increasing, SecureVault CLI provides a simple yet powerful way to secure files using strong encryption.

This project is suitable for:
- Final Year Engineering / BCA / MCA Projects
- Cybersecurity demonstrations
- Secure local data storage
- Learning modern cryptography implementation

---

## 2. Problem Statement

Most users store personal and confidential files in plain form on their systems. If a system is stolen, hacked, or infected with malware, these files can be easily accessed.

Existing encryption tools are often:
- Complex to use
- Paid or proprietary
- Not transparent in implementation

**SecureVault CLI solves this problem** by providing a transparent, secure, and easy-to-use encryption solution built with industry-standard algorithms.

---

## 3. Objectives of the Project

- To develop a secure file encryption system
- To protect files using password-based encryption
- To ensure data confidentiality and integrity
- To support encryption of any file type
- To build a modern CLI-based security tool

---

## 4. Key Features

### 🔐 Strong Encryption
- Uses **AES-256-GCM** (Advanced Encryption Standard)
- Provides both **confidentiality and integrity**

### 🔑 Password-Based Security
- User-defined password
- Password is never stored
- Key derived using **PBKDF2 with Salt**

### 📁 Universal File Support
- Supports **all file types**:
  - PDF, DOCX, TXT
  - Images (JPG, PNG)
  - Audio (MP3, WAV)
  - Video (MP4, MKV)
  - ZIP and binary files

### 🧪 Tamper Detection
- Detects file modification
- Prevents decryption if encrypted file is altered

### ⚡ Large File Support
- Stream-based encryption
- Works efficiently with large files

### 🖥 CLI-Based Professional Tool
- Lightweight
- No GUI dependency
- Platform independent (Windows, Linux, macOS)

---

## 5. Technologies Used

| Component | Technology |
|--------|-----------|
| Language | Java (JDK 17+) |
| Encryption | AES-256-GCM |
| Key Derivation | PBKDF2WithHmacSHA256 |
| Randomness | SecureRandom |
| Interface | Command Line (CLI) |

---

## 6. System Architecture

```
User Password
     ↓
PBKDF2 Key Derivation
     ↓
AES-256-GCM Engine
     ↓
Encrypted File (.enc)
```

During decryption, the same process is reversed using the correct password.

---

## 7. How Encryption Works

1. User selects a file to encrypt
2. User enters and confirms a password
3. A random **salt and IV** are generated
4. PBKDF2 derives a secure 256-bit key
5. File data is encrypted using AES-GCM
6. Output file is saved with `.enc` extension

The encrypted file appears as **random unreadable data**.

---

## 8. How Decryption Works

1. User selects the encrypted `.enc` file
2. User enters the password
3. Salt and IV are read from file header
4. Key is re-derived using PBKDF2
5. AES-GCM verifies authenticity
6. Original file is restored if password is correct

If the password is incorrect or file is modified, decryption fails.

---

## 9. How to Use SecureVault CLI

### Compile the Project
```
javac securevault/**/*.java
```

### Encrypt a File
```
Command:-
java securevault.SecureVaultCLI encrypt
java securevault.SecureVaultCLI encrypt --input "C:\file.pdf" --output "C:\file.pdf.enc"

```
- Enter password
- Confirm password
- Encrypted file will be created with `.enc` extension

### Decrypt a File
```
Command:-
java securevault.SecureVaultCLI decrypt
java securevault.SecureVaultCLI decrypt --input "C:\file.pdf.enc" --output "C:\file.pdf"

```
- Enter password
- Original file will be restored

---

## 10. Security Analysis

| Threat | Protection |
|------|-----------|
| Unauthorized access | AES-256 encryption |
| Brute-force attack | PBKDF2 with salt |
| File tampering | GCM authentication |
| Password leakage | No password storage |

SecureVault CLI follows **zero-trust security principles**.

---

## 11. Applications in Real World

- Personal data protection
- Secure academic documents
- Protecting business files
- Offline secure backups
- Cybersecurity education

---

## 12. Advantages

- Free and open implementation
- Strong modern cryptography
- Easy to use
- Lightweight
- Platform independent

---

## 13. Limitations

- No GUI (CLI-based)
- Password recovery not possible (by design)

---

## 14. Future Enhancements

- Folder encryption
- Progress indicator
- Secure file deletion
- Digital signature support
- GUI version
- Hardware key support

---

## 15. Conclusion

SecureVault CLI is a **modern, secure, and professional file encryption system** built using industry best practices. It demonstrates strong understanding of cryptography, secure software design, and practical implementation.

This project is suitable for academic evaluation as well as real-world usage.

---

## 16. Developer Information

**Project Name:** SecureVault CLI  
**Category:** Cybersecurity / Cryptography  
**Year:** 2026  
**Platform:** Java (Cross-platform)

---

*End of Documentation*

