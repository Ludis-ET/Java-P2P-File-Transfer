package com.p2p.utils;

import java.io.*;
import java.util.Properties;

/**
 * Utility class for loading environment variables from .env file
 */
public class EnvLoader {
    private static Properties envProperties;
    private static final String ENV_FILE = ".env";
    
    static {
        loadEnvFile();
    }
    
    private static void loadEnvFile() {
        envProperties = new Properties();
        
        File envFile = new File(ENV_FILE);
        if (envFile.exists()) {
            try (FileInputStream fis = new FileInputStream(envFile);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    // Skip empty lines and comments
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    
                    // Parse key=value pairs
                    int equalIndex = line.indexOf('=');
                    if (equalIndex > 0) {
                        String key = line.substring(0, equalIndex).trim();
                        String value = line.substring(equalIndex + 1).trim();
                        
                        // Remove quotes if present
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        
                        envProperties.setProperty(key, value);
                    }
                }
                
                System.out.println("Environment variables loaded from " + ENV_FILE);
                
            } catch (IOException e) {
                System.err.println("Failed to load .env file: " + e.getMessage());
                setDefaultValues();
            }
        } else {
            System.out.println(".env file not found, using default configuration");
            setDefaultValues();
        }
    }
    
    private static void setDefaultValues() {
        // Set default values if .env file is not available
        envProperties.setProperty("DB_URL", "jdbc:mysql://localhost:3306/p2p_system");
        envProperties.setProperty("DB_USER", "root");
        envProperties.setProperty("DB_PASSWORD", "");
        envProperties.setProperty("DB_DRIVER", "com.mysql.cj.jdbc.Driver");
        envProperties.setProperty("DEFAULT_PORT", "8888");
        envProperties.setProperty("MAX_FILE_SIZE", "1073741824");
        envProperties.setProperty("CONNECTION_TIMEOUT", "10000");
        envProperties.setProperty("BUFFER_SIZE", "8192");
        envProperties.setProperty("DOWNLOADS_DIRECTORY", "downloads");
        envProperties.setProperty("SERVER_HOST", "localhost");
        envProperties.setProperty("SERVER_PORT", "8888");
    }
    
    /**
     * Get environment variable value
     */
    public static String getEnv(String key) {
        // First check system environment variables
        String systemEnv = System.getenv(key);
        if (systemEnv != null && !systemEnv.isEmpty()) {
            return systemEnv;
        }
        
        // Then check .env file
        return envProperties.getProperty(key);
    }
    
    /**
     * Get environment variable with default value
     */
    public static String getEnv(String key, String defaultValue) {
        String value = getEnv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Get integer environment variable
     */
    public static int getEnvInt(String key, int defaultValue) {
        try {
            String value = getEnv(key);
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get long environment variable
     */
    public static long getEnvLong(String key, long defaultValue) {
        try {
            String value = getEnv(key);
            return (value != null) ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Get boolean environment variable
     */
    public static boolean getEnvBoolean(String key, boolean defaultValue) {
        String value = getEnv(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Check if environment variable exists
     */
    public static boolean hasEnv(String key) {
        return getEnv(key) != null;
    }
    
    /**
     * Reload environment variables from .env file
     */
    public static void reload() {
        loadEnvFile();
    }
    
    /**
     * Get all environment properties
     */
    public static Properties getAllEnvProperties() {
        return new Properties(envProperties);
    }
    
    // Convenience methods for common database configurations
    public static String getDatabaseUrl() {
        return getEnv("DB_URL", "jdbc:mysql://localhost:3306/p2p_system");
    }
    
    public static String getDatabaseUser() {
        return getEnv("DB_USER", "root");
    }
    
    public static String getDatabasePassword() {
        return getEnv("DB_PASSWORD", "");
    }
    
    public static String getDatabaseDriver() {
        return getEnv("DB_DRIVER", "com.mysql.cj.jdbc.Driver");
    }
    
    public static int getDefaultPort() {
        return getEnvInt("DEFAULT_PORT", 8888);
    }
    
    public static long getMaxFileSize() {
        return getEnvLong("MAX_FILE_SIZE", 1073741824L);
    }
    
    public static int getConnectionTimeout() {
        return getEnvInt("CONNECTION_TIMEOUT", 10000);
    }
    
    public static String getDownloadsDirectory() {
        return getEnv("DOWNLOADS_DIRECTORY", "downloads");
    }
}
