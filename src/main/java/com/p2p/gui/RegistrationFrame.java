package com.p2p.gui;

import com.p2p.database.DatabaseManager;
import com.p2p.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

/**
 * Registration window for new user creation
 */
public class RegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private LoginFrame parentFrame;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public RegistrationFrame(LoginFrame parent) {
        this.parentFrame = parent;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("P2P File Transfer - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(parent);
        pack();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
        statusLabel = new JLabel(" ");
        
        // Style components
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        usernameField.setFont(fieldFont);
        emailField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);
        
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        registerButton.setFont(buttonFont);
        cancelButton.setFont(buttonFont);
        
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(220, 220, 220));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Join the P2P network today");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Email
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(emailField, gbc);
        
        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Confirm Password
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(confirmPasswordField, gbc);
        
        // Status label
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(statusLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        Dimension buttonSize = new Dimension(100, 35);
        registerButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Create footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel footerLabel = new JLabel("© 2024 P2P File Transfer System");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToLogin();
            }
        });
        
        // Enter key support on last field
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showStatus("Please fill in all fields", Color.RED);
            return;
        }
        
        if (username.length() < 3) {
            showStatus("Username must be at least 3 characters long", Color.RED);
            return;
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showStatus("Please enter a valid email address", Color.RED);
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters long", Color.RED);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match", Color.RED);
            return;
        }
        
        // Disable button during registration
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        
        // Perform registration in background thread
        SwingWorker<Boolean, Void> registrationWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                DatabaseManager dbManager = DatabaseManager.getInstance();
                
                // Check if username already exists
                if (dbManager.isUsernameExists(username)) {
                    throw new Exception("Username already exists");
                }
                
                // Create new user
                User newUser = new User(username, password, email);
                return dbManager.registerUser(newUser);
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        showStatus("Registration successful! You can now login.", Color.GREEN);
                        
                        // Return to login after 2 seconds
                        Timer timer = new Timer(2000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                returnToLogin();
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus("Registration failed. Please try again.", Color.RED);
                    }
                } catch (Exception e) {
                    showStatus("Registration failed: " + e.getMessage(), Color.RED);
                } finally {
                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                }
            }
        };
        
        registrationWorker.execute();
    }

    private void returnToLogin() {
        this.dispose();
        parentFrame.showLoginWindow();
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}
