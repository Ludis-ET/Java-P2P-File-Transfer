package com.p2p.utils;

import java.io.*;
import java.util.Properties;

/**
 * Utility class for application configuration
 */
public class ConfigUtils {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        properties = new Properties();

        // Set default values
        setDefaults();

        // Try to load from file
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Failed to load configuration: " + e.getMessage());
            }
        } else {
            // Create default config file
            saveConfiguration();
        }
    }

    private static void setDefaults() {
        properties.setProperty("default.port", "8888");
        properties.setProperty("max.file.size", "1073741824"); // 1GB
        properties.setProperty("connection.timeout", "10000"); // 10 seconds
        properties.setProperty("buffer.size", "8192");
        properties.setProperty("downloads.directory", "downloads");

        // Database configuration
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/p2p_system");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "P2P File Transfer System Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long getLongProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    // Convenience methods for common configurations
    // These methods now integrate with EnvLoader for .env support
    public static int getDefaultPort() {
        // First try .env, then properties file, then default
        int envPort = EnvLoader.getEnvInt("DEFAULT_PORT", -1);
        return envPort != -1 ? envPort : getIntProperty("default.port", 8888);
    }

    public static long getMaxFileSize() {
        long envSize = EnvLoader.getEnvLong("MAX_FILE_SIZE", -1);
        return envSize != -1 ? envSize : getLongProperty("max.file.size", 1073741824L);
    }

    public static int getConnectionTimeout() {
        int envTimeout = EnvLoader.getEnvInt("CONNECTION_TIMEOUT", -1);
        return envTimeout != -1 ? envTimeout : getIntProperty("connection.timeout", 10000);
    }

    public static int getBufferSize() {
        int envBuffer = EnvLoader.getEnvInt("BUFFER_SIZE", -1);
        return envBuffer != -1 ? envBuffer : getIntProperty("buffer.size", 8192);
    }

    public static String getDownloadsDirectory() {
        String envDir = EnvLoader.getEnv("DOWNLOADS_DIRECTORY");
        return envDir != null ? envDir : getProperty("downloads.directory", "downloads");
    }

    public static String getDatabaseUrl() {
        return EnvLoader.getDatabaseUrl();
    }

    public static String getDatabaseUser() {
        return EnvLoader.getDatabaseUser();
    }

    public static String getDatabasePassword() {
        return EnvLoader.getDatabasePassword();
    }

    public static String getDatabaseDriver() {
        return EnvLoader.getDatabaseDriver();
    }
}
