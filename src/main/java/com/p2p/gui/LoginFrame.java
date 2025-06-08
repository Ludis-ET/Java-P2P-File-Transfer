package com.p2p.gui;

import com.p2p.database.DatabaseManager;
import com.p2p.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login window for user authentication
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        setTitle("P2P File Transfer - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        statusLabel = new JLabel(" ");
        
        // Style components
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        
        Font buttonFont = new Font("Arial", Font.BOLD, 12);
        loginButton.setFont(buttonFont);
        registerButton.setFont(buttonFont);
        
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
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
        JLabel titleLabel = new JLabel("P2P File Transfer System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Secure • Fast • Reliable");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(128, 128, 128));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username label and field
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Status label
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(statusLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        Dimension buttonSize = new Dimension(100, 35);
        loginButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridy = 5;
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
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationWindow();
            }
        });
        
        // Enter key support
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocus();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password", Color.RED);
            return;
        }
        
        // Disable button during login attempt
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        // Perform login in background thread
        SwingWorker<User, Void> loginWorker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return DatabaseManager.getInstance().authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        showStatus("Login successful!", Color.GREEN);
                        openDashboard(user);
                    } else {
                        showStatus("Invalid username or password", Color.RED);
                    }
                } catch (Exception e) {
                    showStatus("Login failed: " + e.getMessage(), Color.RED);
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        
        loginWorker.execute();
    }

    private void openRegistrationWindow() {
        RegistrationFrame registrationFrame = new RegistrationFrame(this);
        registrationFrame.setVisible(true);
        this.setVisible(false);
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            DashboardFrame dashboard = new DashboardFrame(user);
            dashboard.setVisible(true);
            this.dispose();
        });
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    public void showLoginWindow() {
        this.setVisible(true);
    }
}
