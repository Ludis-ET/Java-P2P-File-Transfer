package com.p2p.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for secure password hashing and verification
 */
public class PasswordUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = "$";
    
    /**
     * Generate a random salt
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hash a password with salt
     */
    private static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Create a secure hash of the password with salt
     * Returns: salt$hashedPassword
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        byte[] salt = generateSalt();
        String hashedPassword = hashPassword(plainTextPassword, salt);
        String saltString = Base64.getEncoder().encodeToString(salt);
        
        return saltString + SEPARATOR + hashedPassword;
    }
    
    /**
     * Verify a password against its hash
     */
    public static boolean verifyPassword(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        
        try {
            // Split the stored hash to get salt and hash
            String[] parts = storedHash.split("\\" + SEPARATOR);
            if (parts.length != 2) {
                // Legacy plain text password support (for backward compatibility)
                return plainTextPassword.equals(storedHash);
            }
            
            String saltString = parts[0];
            String storedPasswordHash = parts[1];
            
            // Decode the salt
            byte[] salt = Base64.getDecoder().decode(saltString);
            
            // Hash the provided password with the same salt
            String hashedProvidedPassword = hashPassword(plainTextPassword, salt);
            
            // Compare the hashes
            return hashedProvidedPassword.equals(storedPasswordHash);
            
        } catch (Exception e) {
            // If there's any error, try comparing as plain text (legacy support)
            return plainTextPassword.equals(storedHash);
        }
    }
    
    /**
     * Check if a password hash is using the new secure format
     */
    public static boolean isSecureHash(String hash) {
        return hash != null && hash.contains(SEPARATOR) && hash.split("\\" + SEPARATOR).length == 2;
    }
    
    /**
     * Validate password strength
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        
        // For now, just check minimum length (can be enhanced later)
        return password.length() >= 6;
    }
    
    /**
     * Get password strength score (0-4)
     */
    public static int getPasswordStrengthScore(String password) {
        if (password == null) return 0;
        
        int score = 0;
        
        // Length check
        if (password.length() >= 6) score++;
        if (password.length() >= 8) score++;
        
        // Character type checks
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) >= 0);
        
        if (hasUpper && hasLower) score++;
        if (hasDigit) score++;
        if (hasSpecial) score++;
        
        return Math.min(score, 4); // Max score is 4
    }
    
    /**
     * Get password strength description
     */
    public static String getPasswordStrengthDescription(String password) {
        int score = getPasswordStrengthScore(password);
        switch (score) {
            case 0:
            case 1: return "Weak";
            case 2: return "Fair";
            case 3: return "Good";
            case 4: return "Strong";
            default: return "Unknown";
        }
    }
}