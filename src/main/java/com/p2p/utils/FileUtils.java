package com.p2p.utils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Utility class for file operations
 */
public class FileUtils {
    private static final DecimalFormat df = new DecimalFormat("#.##");
    
    /**
     * Format file size in human readable format
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return df.format(bytes / 1024.0) + " KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return df.format(bytes / (1024.0 * 1024.0)) + " MB";
        } else {
            return df.format(bytes / (1024.0 * 1024.0 * 1024.0)) + " GB";
        }
    }
    
    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * Get file name without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
    
    /**
     * Validate file for transfer
     */
    public static boolean isValidFileForTransfer(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        if (!file.isFile()) {
            return false;
        }
        
        if (!file.canRead()) {
            return false;
        }
        
        // Check file size (limit to 1GB for this demo)
        long maxSize = 1024L * 1024L * 1024L; // 1GB
        if (file.length() > maxSize) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get safe file name for storage
     */
    public static String getSafeFileName(String fileName) {
        // Remove or replace unsafe characters
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
