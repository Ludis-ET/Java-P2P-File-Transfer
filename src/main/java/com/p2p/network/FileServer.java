package com.p2p.network;

import com.p2p.gui.DashboardFrame;
import com.p2p.model.Transfer;
import com.p2p.model.TransferType;
import com.p2p.utils.FileUtils;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * File server for receiving files from peers
 */
public class FileServer implements Runnable {
    private ServerSocket serverSocket;
    private int port;
    private DashboardFrame dashboard;
    private volatile boolean running = false;
    private ExecutorService clientExecutor;
    
    private static final String DOWNLOADS_DIR = "downloads";
    private static final int BUFFER_SIZE = 8192;

    public FileServer(int port, DashboardFrame dashboard) throws IOException {
        this.port = port;
        this.dashboard = dashboard;
        this.serverSocket = new ServerSocket(port);
        this.clientExecutor = Executors.newCachedThreadPool();
        
        // Create downloads directory if it doesn't exist
        createDownloadsDirectory();
    }

    private void createDownloadsDirectory() {
        try {
            Path downloadsPath = Paths.get(DOWNLOADS_DIR);
            if (!Files.exists(downloadsPath)) {
                Files.createDirectories(downloadsPath);
                dashboard.addLog("Created downloads directory: " + downloadsPath.toAbsolutePath());
            }
        } catch (IOException e) {
            dashboard.addLog("Failed to create downloads directory: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        running = true;
        dashboard.addLog("File server listening on port " + port);
        
        while (running && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                dashboard.addLog("Incoming connection from: " + clientSocket.getInetAddress());
                
                // Handle each client in a separate thread
                clientExecutor.submit(() -> handleClient(clientSocket));
                
            } catch (IOException e) {
                if (running) {
                    dashboard.addLog("Server error: " + e.getMessage());
                }
            }
        }
        
        dashboard.addLog("File server stopped");
    }

    private void handleClient(Socket clientSocket) {
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {
            
            // Read file information
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();
            String senderUsername = dis.readUTF();
            
            dashboard.addLog("Receiving file: " + fileName + " (" + 
                FileUtils.formatFileSize(fileSize) + ") from " + senderUsername);
            
            // Send acknowledgment
            dos.writeUTF("READY");
            dos.flush();
            
            // Prepare file path
            Path filePath = Paths.get(DOWNLOADS_DIR, fileName);
            
            // Handle file name conflicts
            int counter = 1;
            while (Files.exists(filePath)) {
                String nameWithoutExt = FileUtils.getFileNameWithoutExtension(fileName);
                String extension = FileUtils.getFileExtension(fileName);
                String newFileName = nameWithoutExt + "_" + counter + 
                    (extension.isEmpty() ? "" : "." + extension);
                filePath = Paths.get(DOWNLOADS_DIR, newFileName);
                counter++;
            }
            
            // Create transfer record
            Transfer transfer = new Transfer(
                dashboard.getCurrentUser().getUserId(),
                filePath.getFileName().toString(),
                fileSize,
                TransferType.RECEIVED,
                senderUsername
            );
            transfer.setPeerIpAddress(clientSocket.getInetAddress().getHostAddress());
            transfer.setFilePath(filePath.toString());
            transfer.setStatus(Transfer.TransferStatus.IN_PROGRESS);
            
            // Receive file
            boolean success = receiveFile(dis, filePath, fileSize, transfer);
            
            if (success) {
                dos.writeUTF("SUCCESS");
                transfer.setStatus(Transfer.TransferStatus.COMPLETED);
                dashboard.addLog("File received successfully: " + filePath.getFileName());
            } else {
                dos.writeUTF("FAILED");
                transfer.setStatus(Transfer.TransferStatus.FAILED);
                dashboard.addLog("Failed to receive file: " + fileName);
                
                // Clean up partial file
                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    dashboard.addLog("Failed to clean up partial file: " + e.getMessage());
                }
            }
            
            dos.flush();
            
            // Notify dashboard
            dashboard.onFileReceived(transfer);
            
        } catch (IOException e) {
            dashboard.addLog("Client handling error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                dashboard.addLog("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private boolean receiveFile(DataInputStream dis, Path filePath, long fileSize, Transfer transfer) {
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            long totalBytesReceived = 0;
            int lastProgress = 0;
            
            while (totalBytesReceived < fileSize) {
                int bytesToRead = (int) Math.min(buffer.length, fileSize - totalBytesReceived);
                int bytesRead = dis.read(buffer, 0, bytesToRead);
                
                if (bytesRead == -1) {
                    dashboard.addLog("Unexpected end of stream while receiving file");
                    return false;
                }
                
                bos.write(buffer, 0, bytesRead);
                totalBytesReceived += bytesRead;
                
                // Update progress
                int progress = (int) ((totalBytesReceived * 100) / fileSize);
                if (progress != lastProgress) {
                    lastProgress = progress;
                    dashboard.onTransferProgress(progress, 
                        "Receiving: " + progress + "% (" + 
                        FileUtils.formatFileSize(totalBytesReceived) + "/" + 
                        FileUtils.formatFileSize(fileSize) + ")");
                }
            }
            
            bos.flush();
            return true;
            
        } catch (IOException e) {
            dashboard.addLog("Error receiving file: " + e.getMessage());
            return false;
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (clientExecutor != null && !clientExecutor.isShutdown()) {
                clientExecutor.shutdown();
            }
        } catch (IOException e) {
            dashboard.addLog("Error stopping server: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }
}
