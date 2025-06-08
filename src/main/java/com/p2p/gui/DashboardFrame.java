package com.p2p.gui;

import com.p2p.database.DatabaseManager;
import com.p2p.model.User;
import com.p2p.model.Transfer;
import com.p2p.model.TransferType;
import com.p2p.network.FileServer;
import com.p2p.network.FileClient;
import com.p2p.utils.FileUtils;
import com.p2p.utils.ConfigUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main dashboard window for authenticated users
 */
public class DashboardFrame extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    private JTextField peerIpField;
    private JTextField peerPortField;
    private JButton selectFileButton;
    private JButton sendFileButton;
    private JButton refreshLogsButton;
    private JButton logoutButton;
    private JTable transferTable;
    private DefaultTableModel tableModel;
    private JLabel selectedFileLabel;
    private JProgressBar transferProgressBar;
    private JTextArea logArea;

    private File selectedFile;
    private FileServer fileServer;
    private ExecutorService executorService;

    private static final int DEFAULT_PORT = ConfigUtils.getDefaultPort();

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.executorService = Executors.newCachedThreadPool();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTransferHistory();
        startFileServer();

        setTitle("P2P File Transfer - Dashboard (" + user.getUsername() + ")");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Update user online status
        updateUserOnlineStatus(true);
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        statusLabel = new JLabel("Ready to transfer files");
        peerIpField = new JTextField("127.0.0.1", 15);
        peerPortField = new JTextField(String.valueOf(DEFAULT_PORT), 8);
        selectFileButton = new JButton("Select File");
        sendFileButton = new JButton("Send File");
        refreshLogsButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
        selectedFileLabel = new JLabel("No file selected");
        transferProgressBar = new JProgressBar(0, 100);
        logArea = new JTextArea(8, 50);

        // Style components
        Font headerFont = new Font("Arial", Font.BOLD, 18);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        welcomeLabel.setFont(headerFont);
        welcomeLabel.setForeground(new Color(51, 51, 51));

        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(60, 179, 113));

        peerIpField.setFont(fieldFont);
        peerPortField.setFont(fieldFont);

        selectFileButton.setBackground(new Color(70, 130, 180));
        selectFileButton.setForeground(Color.WHITE);
        selectFileButton.setFocusPainted(false);

        sendFileButton.setBackground(new Color(60, 179, 113));
        sendFileButton.setForeground(Color.WHITE);
        sendFileButton.setFocusPainted(false);
        sendFileButton.setEnabled(false);

        refreshLogsButton.setBackground(new Color(100, 149, 237));
        refreshLogsButton.setForeground(Color.WHITE);
        refreshLogsButton.setFocusPainted(false);

        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);

        selectedFileLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        selectedFileLabel.setForeground(Color.GRAY);

        transferProgressBar.setStringPainted(true);
        transferProgressBar.setString("No active transfer");

        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(new Color(248, 248, 248));

        // Initialize transfer table
        String[] columnNames = {"Type", "File Name", "Size", "Peer", "Status", "Timestamp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transferTable = new JTable(tableModel);
        transferTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transferTable.setRowHeight(25);

        // Configure table columns
        TableColumnModel columnModel = transferTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Type
        columnModel.getColumn(1).setPreferredWidth(200); // File Name
        columnModel.getColumn(2).setPreferredWidth(80);  // Size
        columnModel.getColumn(3).setPreferredWidth(120); // Peer
        columnModel.getColumn(4).setPreferredWidth(100); // Status
        columnModel.getColumn(5).setPreferredWidth(150); // Timestamp
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(Color.WHITE);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.add(welcomeLabel);
        userInfoPanel.add(Box.createHorizontalStrut(20));
        userInfoPanel.add(statusLabel);

        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtonPanel.setBackground(Color.WHITE);
        headerButtonPanel.add(refreshLogsButton);
        headerButtonPanel.add(logoutButton);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // File transfer panel
        JPanel transferPanel = createFileTransferPanel();
        mainPanel.add(transferPanel, BorderLayout.NORTH);

        // Split pane for history and logs
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.6);

        // Transfer history panel
        JPanel historyPanel = createTransferHistoryPanel();
        splitPane.setTopComponent(historyPanel);

        // Log panel
        JPanel logPanel = createLogPanel();
        splitPane.setBottomComponent(logPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFileTransferPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "File Transfer (* Required)",
            0, 0, new Font("Arial", Font.BOLD, 14)));
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Peer connection section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel peerIpLabel = new JLabel("Peer IP Address: *");
        peerIpLabel.setFont(new Font("Arial", Font.BOLD, 12));
        peerIpLabel.setForeground(new Color(51, 51, 51));
        peerIpLabel.setToolTipText("Enter the IP address of the peer you want to send files to");
        panel.add(peerIpLabel, gbc);

        gbc.gridx = 1;
        peerIpField.setToolTipText("Enter peer's IP address (e.g., 192.168.1.100)");
        peerIpField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        panel.add(peerIpField, gbc);

        gbc.gridx = 2;
        JLabel portLabel = new JLabel("Port: *");
        portLabel.setFont(new Font("Arial", Font.BOLD, 12));
        portLabel.setForeground(new Color(51, 51, 51));
        portLabel.setToolTipText("Enter the port number (default: 8888)");
        panel.add(portLabel, gbc);

        gbc.gridx = 3;
        peerPortField.setToolTipText("Enter peer's port number (default: 8888)");
        peerPortField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        panel.add(peerPortField, gbc);

        // File selection section
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(selectFileButton, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(selectedFileLabel, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(sendFileButton, gbc);

        // Progress bar
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(transferProgressBar, gbc);

        return panel;
    }

    private JPanel createTransferHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Transfer History",
            0, 0, new Font("Arial", Font.BOLD, 14)));

        JScrollPane scrollPane = new JScrollPane(transferTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Activity Log",
            0, 0, new Font("Arial", Font.BOLD, 14)));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupEventHandlers() {
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        refreshLogsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTransferHistory();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Window closing handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select File to Send");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText("Selected: " + selectedFile.getName() +
                " (" + FileUtils.formatFileSize(selectedFile.length()) + ")");
            sendFileButton.setEnabled(true);
            addLog("File selected: " + selectedFile.getName());
        }
    }

    private void sendFile() {
        if (selectedFile == null) {
            showError("Please select a file first");
            return;
        }

        String peerIp = peerIpField.getText().trim();
        String peerPortText = peerPortField.getText().trim();

        if (peerIp.isEmpty() || peerPortText.isEmpty()) {
            showError("Please enter peer IP and port");
            return;
        }

        try {
            int peerPort = Integer.parseInt(peerPortText);

            // Create transfer record
            Transfer transfer = new Transfer(
                currentUser.getUserId(),
                selectedFile.getName(),
                selectedFile.length(),
                TransferType.SENT,
                "Unknown" // We don't know the peer username
            );
            transfer.setPeerIpAddress(peerIp);
            transfer.setFilePath(selectedFile.getAbsolutePath());

            // Log transfer to database
            DatabaseManager.getInstance().logTransfer(transfer);

            // Disable UI during transfer
            sendFileButton.setEnabled(false);
            selectFileButton.setEnabled(false);

            // Start file transfer in background
            FileClient fileClient = new FileClient(this);
            executorService.submit(() -> {
                fileClient.sendFile(selectedFile, peerIp, peerPort, transfer);
            });

            addLog("Starting file transfer to " + peerIp + ":" + peerPort);
            updateStatus("Transferring file...");

        } catch (NumberFormatException e) {
            showError("Invalid port number");
        }
    }

    private void startFileServer() {
        try {
            fileServer = new FileServer(DEFAULT_PORT, this);
            executorService.submit(fileServer);
            addLog("File server started on port " + DEFAULT_PORT);
            updateStatus("Ready to receive files");
        } catch (Exception e) {
            addLog("Failed to start file server: " + e.getMessage());
            updateStatus("Server startup failed");
        }
    }

    private void loadTransferHistory() {
        SwingWorker<List<Transfer>, Void> worker = new SwingWorker<List<Transfer>, Void>() {
            @Override
            protected List<Transfer> doInBackground() throws Exception {
                return DatabaseManager.getInstance().getUserTransfers(currentUser.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<Transfer> transfers = get();
                    updateTransferTable(transfers);
                } catch (Exception e) {
                    addLog("Failed to load transfer history: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void updateTransferTable(List<Transfer> transfers) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transfer transfer : transfers) {
            Object[] row = {
                transfer.getTransferType().getDisplayName(),
                transfer.getFileName(),
                transfer.getFormattedFileSize(),
                transfer.getPeerUsername() != null ? transfer.getPeerUsername() : transfer.getPeerIpAddress(),
                transfer.getStatus().name(),
                transfer.getTimestamp().format(formatter)
            };
            tableModel.addRow(row);
        }
    }

    private void updateUserOnlineStatus(boolean isOnline) {
        DatabaseManager.getInstance().updateUserOnlineStatus(
            currentUser.getUserId(),
            isOnline,
            "127.0.0.1",
            DEFAULT_PORT
        );
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            // Stop file server
            if (fileServer != null) {
                fileServer.stop();
            }

            // Shutdown executor service
            executorService.shutdown();

            // Update user offline status
            updateUserOnlineStatus(false);

            // Return to login
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
                dispose();
            });
        }
    }

    // Public methods for file transfer callbacks
    public void onTransferProgress(int progress, String message) {
        SwingUtilities.invokeLater(() -> {
            transferProgressBar.setValue(progress);
            transferProgressBar.setString(message);
        });
    }

    public void onTransferComplete(Transfer transfer, boolean success) {
        SwingUtilities.invokeLater(() -> {
            // Update transfer status in database
            Transfer.TransferStatus status = success ?
                Transfer.TransferStatus.COMPLETED : Transfer.TransferStatus.FAILED;
            DatabaseManager.getInstance().updateTransferStatus(transfer.getTransferId(), status);

            // Reset UI
            sendFileButton.setEnabled(selectedFile != null);
            selectFileButton.setEnabled(true);
            transferProgressBar.setValue(0);
            transferProgressBar.setString("No active transfer");

            // Update status and logs
            String message = success ? "Transfer completed successfully" : "Transfer failed";
            updateStatus(message);
            addLog(message + ": " + transfer.getFileName());

            // Refresh history
            loadTransferHistory();
        });
    }

    public void onFileReceived(Transfer transfer) {
        SwingUtilities.invokeLater(() -> {
            // Log the received transfer
            DatabaseManager.getInstance().logTransfer(transfer);
            addLog("File received: " + transfer.getFileName() + " from " + transfer.getPeerIpAddress());
            loadTransferHistory();
        });
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
