package com.p2p;

import com.p2p.gui.LoginFrame;
import com.p2p.database.DatabaseManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class for P2P File Transfer Application
 * Entry point that initializes the database and launches the GUI
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }

        // Initialize database on startup
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseManager.getInstance().initializeDatabase();
                System.out.println("Database initialized successfully");

                // Launch the login window
                new LoginFrame().setVisible(true);

            } catch (Exception e) {
                System.err.println("Failed to initialize application: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
