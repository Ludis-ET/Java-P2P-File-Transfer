package com.p2p.gui;

import com.p2p.database.DatabaseManager;
import com.p2p.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
        gbc.insets = new Insets(0, 10, 10, 10);
        mainPanel.add(subtitleLabel, gbc);

        // Form instructions panel
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBackground(new Color(240, 248, 255));
        instructionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));


        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(instructionsPanel, gbc);

        // Username Section with enhanced labels
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        // Username label with icon
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        usernamePanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("👤 Username: *");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(new Color(51, 51, 51));
        usernameLabel.setToolTipText("Choose a unique username (minimum 3 characters)");
        usernamePanel.add(usernameLabel);
        mainPanel.add(usernamePanel, gbc);

        // Username description
        gbc.gridy = 2;
        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 2, 10);
        JLabel usernameDesc = new JLabel("Your unique identifier (3+ characters)");
        usernameDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        usernameDesc.setForeground(new Color(128, 128, 128));
        mainPanel.add(usernameDesc, gbc);

        // Username field
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField.setToolTipText("Enter a unique username (minimum 3 characters)");
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        usernameField.setBackground(Color.WHITE);
        mainPanel.add(usernameField, gbc);

        // Email Section
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        // Email label with icon
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailPanel.setBackground(Color.WHITE);
        JLabel emailLabel = new JLabel("📧 Email Address: *");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(new Color(51, 51, 51));
        emailLabel.setToolTipText("Enter a valid email address");
        emailPanel.add(emailLabel);
        mainPanel.add(emailPanel, gbc);

        // Email description
        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 2, 10);
        JLabel emailDesc = new JLabel("Valid email address (e.g., user@example.com)");
        emailDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        emailDesc.setForeground(new Color(128, 128, 128));
        mainPanel.add(emailDesc, gbc);

        // Email field
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField.setToolTipText("Enter your email address (e.g., user@example.com)");
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        emailField.setBackground(Color.WHITE);
        mainPanel.add(emailField, gbc);

        // Password Section
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        // Password label with icon
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordPanel.setBackground(Color.WHITE);
        JLabel passwordLabel = new JLabel("🔒 Password: *");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(51, 51, 51));
        passwordLabel.setToolTipText("Choose a secure password (minimum 6 characters)");
        passwordPanel.add(passwordLabel);
        mainPanel.add(passwordPanel, gbc);

        // Password description
        gbc.gridy = 6;
        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 2, 10);
        JLabel passwordDesc = new JLabel("Secure password (6+ characters)");
        passwordDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        passwordDesc.setForeground(new Color(128, 128, 128));
        mainPanel.add(passwordDesc, gbc);

        // Password field
        gbc.gridy = 7;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField.setToolTipText("Enter a secure password (minimum 6 characters)");
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        passwordField.setBackground(Color.WHITE);
        mainPanel.add(passwordField, gbc);

        // Confirm Password Section
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        // Confirm Password label with icon
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        confirmPanel.setBackground(Color.WHITE);
        JLabel confirmPasswordLabel = new JLabel("🔐 Confirm Password: *");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(new Color(51, 51, 51));
        confirmPasswordLabel.setToolTipText("Re-enter your password to confirm");
        confirmPanel.add(confirmPasswordLabel);
        mainPanel.add(confirmPanel, gbc);

        // Confirm password description
        gbc.gridy = 8;
        gbc.gridx = 1;
        gbc.insets = new Insets(15, 10, 2, 10);
        JLabel confirmDesc = new JLabel("Re-enter password to confirm");
        confirmDesc.setFont(new Font("Arial", Font.ITALIC, 11));
        confirmDesc.setForeground(new Color(128, 128, 128));
        mainPanel.add(confirmDesc, gbc);

        // Confirm password field
        gbc.gridy = 9;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField.setToolTipText("Re-enter your password to confirm it matches");
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        confirmPasswordField.setBackground(Color.WHITE);
        mainPanel.add(confirmPasswordField, gbc);

        // Status label
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(statusLabel, gbc);

        // Button panel with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        Dimension buttonSize = new Dimension(120, 40);
        registerButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);

        // Enhanced button styling
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 11;
        gbc.insets = new Insets(20, 10, 15, 10);
        gbc.fill = GridBagConstraints.NONE;
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

        // Real-time validation feedback
        addValidationListeners();
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

    private void addValidationListeners() {
        // Username validation
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String username = usernameField.getText().trim();
                if (username.length() >= 3) {
                    usernameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 139, 34), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else if (username.length() > 0) {
                    usernameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 20, 60), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else {
                    usernameField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });

        // Email validation
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String email = emailField.getText().trim();
                if (EMAIL_PATTERN.matcher(email).matches()) {
                    emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 139, 34), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else if (email.length() > 0) {
                    emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 20, 60), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else {
                    emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });

        // Password validation
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordField.getPassword());
                if (password.length() >= 6) {
                    passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 139, 34), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else if (password.length() > 0) {
                    passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 20, 60), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else {
                    passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });

        // Confirm password validation
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                if (confirmPassword.length() > 0 && confirmPassword.equals(password)) {
                    confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(34, 139, 34), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else if (confirmPassword.length() > 0) {
                    confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 20, 60), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                } else {
                    confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}