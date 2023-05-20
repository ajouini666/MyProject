package com.example.packageassignment.service;

import java.io.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.springframework.stereotype.Service;

@Service
public class SecurityEncryption {
    private static final String CODES_FILE = "codes.bin";
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY = "kXp2s5v8y/B?E(H+MbQeThWmYq3t6w9z"; // Replace with your secret key

    private Map<String, List<String>> usedCodes;

    public SecurityEncryption() {
        usedCodes = loadUsedCodes();
    }

    public String generateCode(String input) {
        String code = generateUniqueCode();
        usedCodes.computeIfAbsent(input, k -> new ArrayList<>()).add(code);
        saveUsedCodes();
        return code;
    }

    public String decryptCode(String code) {
        for (Map.Entry<String, List<String>> entry : usedCodes.entrySet()) {
            List<String> codes = entry.getValue();
            if (codes.contains(code)) {
                codes.remove(code);
                if (codes.isEmpty()) {
                    usedCodes.remove(entry.getKey());
                }
                saveUsedCodes();
                return entry.getKey();
            }
        }
        return null; // Invalid code
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (codeExists(code));
        return code;
    }

    private String generateRandomCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CODE_CHARACTERS.length());
            char randomChar = CODE_CHARACTERS.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }
        return codeBuilder.toString();
    }

    private boolean codeExists(String code) {
        for (List<String> codes : usedCodes.values()) {
            if (codes.contains(code)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, List<String>> loadUsedCodes() {
        Map<String, List<String>> codes = new HashMap<>();

        try {
            File file = new File(CODES_FILE);
            if (!file.exists()) {
                return codes;
            }

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
                byte[] encryptedData = (byte[]) inputStream.readObject();
                byte[] decryptedData = cipher.doFinal(encryptedData);

                ByteArrayInputStream byteStream = new ByteArrayInputStream(decryptedData);
                try (ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
                    codes = (Map<String, List<String>>) objectStream.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            // Log the error message instead of printing the stack trace
            System.err.println("Error occurred while loading used codes: " + e.getMessage());
        }

        return codes;
    }

    private void saveUsedCodes() {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
                objectStream.writeObject(usedCodes);
                objectStream.flush();

                byte[] data = byteStream.toByteArray();
                byte[] encryptedData = cipher.doFinal(data);

                try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(CODES_FILE))) {
                    outputStream.writeObject(encryptedData);
                    outputStream.flush();
                }
            }
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            // Log the error message instead of printing the stack trace
            System.err.println("Error occurred while saving used codes: " + e.getMessage());
        }
    }
}