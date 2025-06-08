package com.p2p.database;

import com.p2p.model.User;
import com.p2p.model.Transfer;
import com.p2p.model.TransferType;
import com.p2p.utils.EnvLoader;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for MySQL operations
 * Handles all database interactions including user management and transfer logging
 */
public class DatabaseManager {
    private static DatabaseManager instance;

    // Load database configuration from .env file
    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASSWORD;
    private final String DB_DRIVER;

    private Connection connection;

    private DatabaseManager() {
        // Load configuration from .env file
        this.DB_URL = EnvLoader.getDatabaseUrl();
        this.DB_USER = EnvLoader.getDatabaseUser();
        this.DB_PASSWORD = EnvLoader.getDatabasePassword();
        this.DB_DRIVER = EnvLoader.getDatabaseDriver();

        System.out.println("Database configuration loaded:");
        System.out.println("  URL: " + DB_URL);
        System.out.println("  User: " + DB_USER);
        System.out.println("  Driver: " + DB_DRIVER);

        try {
            // Load MySQL JDBC driver
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            // Try to create database if it doesn't exist
            createDatabaseIfNotExists();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createDatabaseIfNotExists() {
        try {
            // Extract database name from URL
            String databaseName = extractDatabaseName(DB_URL);
            String baseUrl = DB_URL.substring(0, DB_URL.lastIndexOf('/') + 1);

            // Connect without specifying database
            Connection tempConnection = DriverManager.getConnection(baseUrl, DB_USER, DB_PASSWORD);

            Statement stmt = tempConnection.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
            System.out.println("Database '" + databaseName + "' created if not exists");
            stmt.close();
            tempConnection.close();

            // Now connect to the created database
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established after creation");

        } catch (SQLException e) {
            System.err.println("Failed to create database: " + e.getMessage());
        }
    }

    private String extractDatabaseName(String url) {
        // Extract database name from JDBC URL
        String[] parts = url.split("/");
        if (parts.length > 0) {
            String lastPart = parts[parts.length - 1];
            // Remove any query parameters
            int queryIndex = lastPart.indexOf('?');
            return queryIndex > 0 ? lastPart.substring(0, queryIndex) : lastPart;
        }
        return "p2p_system"; // fallback
    }

    public void initializeDatabase() throws SQLException {
        createTables();
    }

    private void createTables() throws SQLException {
        // Create users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_online BOOLEAN DEFAULT FALSE,
                ip_address VARCHAR(45),
                port INT
            )
        """;

        // Create transfers table
        String createTransfersTable = """
            CREATE TABLE IF NOT EXISTS transfers (
                transfer_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                file_name VARCHAR(255) NOT NULL,
                file_size BIGINT NOT NULL,
                transfer_type ENUM('SENT', 'RECEIVED') NOT NULL,
                peer_username VARCHAR(50),
                peer_ip_address VARCHAR(45),
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
                file_path VARCHAR(500),
                FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createTransfersTable);
            System.out.println("Database tables created successfully");
        }
    }

    // User Management Methods
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        return false;
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    user.setOnline(rs.getBoolean("is_online"));
                    user.setIpAddress(rs.getString("ip_address"));
                    user.setPort(rs.getInt("port"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    public void updateUserOnlineStatus(int userId, boolean isOnline, String ipAddress, int port) {
        String sql = "UPDATE users SET is_online = ?, ip_address = ?, port = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isOnline);
            pstmt.setString(2, ipAddress);
            pstmt.setInt(3, port);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
        }
    }

    // Transfer Management Methods
    public boolean logTransfer(Transfer transfer) {
        String sql = """
            INSERT INTO transfers (user_id, file_name, file_size, transfer_type,
            peer_username, peer_ip_address, status, file_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transfer.getUserId());
            pstmt.setString(2, transfer.getFileName());
            pstmt.setLong(3, transfer.getFileSize());
            pstmt.setString(4, transfer.getTransferType().name());
            pstmt.setString(5, transfer.getPeerUsername());
            pstmt.setString(6, transfer.getPeerIpAddress());
            pstmt.setString(7, transfer.getStatus().name());
            pstmt.setString(8, transfer.getFilePath());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transfer.setTransferId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error logging transfer: " + e.getMessage());
        }
        return false;
    }

    public void updateTransferStatus(int transferId, Transfer.TransferStatus status) {
        String sql = "UPDATE transfers SET status = ? WHERE transfer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, transferId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating transfer status: " + e.getMessage());
        }
    }

    public List<Transfer> getUserTransfers(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfers WHERE user_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transfer transfer = new Transfer();
                    transfer.setTransferId(rs.getInt("transfer_id"));
                    transfer.setUserId(rs.getInt("user_id"));
                    transfer.setFileName(rs.getString("file_name"));
                    transfer.setFileSize(rs.getLong("file_size"));
                    transfer.setTransferType(TransferType.valueOf(rs.getString("transfer_type")));
                    transfer.setPeerUsername(rs.getString("peer_username"));
                    transfer.setPeerIpAddress(rs.getString("peer_ip_address"));
                    transfer.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    transfer.setStatus(Transfer.TransferStatus.valueOf(rs.getString("status")));
                    transfer.setFilePath(rs.getString("file_path"));
                    transfers.add(transfer);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user transfers: " + e.getMessage());
        }
        return transfers;
    }

    public List<User> getOnlineUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_online = TRUE";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setOnline(rs.getBoolean("is_online"));
                user.setIpAddress(rs.getString("ip_address"));
                user.setPort(rs.getInt("port"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error getting online users: " + e.getMessage());
        }
        return users;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}