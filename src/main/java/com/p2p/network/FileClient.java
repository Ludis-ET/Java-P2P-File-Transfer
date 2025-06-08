package com.p2p.network;

import com.p2p.gui.DashboardFrame;
import com.p2p.model.Transfer;
import com.p2p.utils.FileUtils;

import java.io.*;
import java.net.*;

/**
 * File client for sending files to peers
 */
public class FileClient {
    private DashboardFrame dashboard;
    private static final int BUFFER_SIZE = 8192;
    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds

    public FileClient(DashboardFrame dashboard) {
        this.dashboard = dashboard;
    }

    public void sendFile(File file, String peerIp, int peerPort, Transfer transfer) {
        Socket socket = null;
        try {
            dashboard.addLog("Connecting to peer: " + peerIp + ":" + peerPort);
            dashboard.onTransferProgress(0, "Connecting to peer...");
            
            // Create socket with timeout
            socket = new Socket();
            socket.connect(new InetSocketAddress(peerIp, peerPort), CONNECTION_TIMEOUT);
            socket.setSoTimeout(30000); // 30 seconds read timeout
            
            dashboard.addLog("Connected to peer, starting file transfer");
            
            try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                 DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                
                // Send file information
                dos.writeUTF(file.getName());
                dos.writeLong(file.length());
                dos.writeUTF(dashboard.getCurrentUser().getUsername());
                dos.flush();
                
                // Wait for acknowledgment
                String response = dis.readUTF();
                if (!"READY".equals(response)) {
                    throw new IOException("Peer not ready to receive file: " + response);
                }
                
                dashboard.addLog("Peer ready, transferring file data");
                transfer.setStatus(Transfer.TransferStatus.IN_PROGRESS);
                
                // Send file data
                boolean success = sendFileData(dos, file, transfer);
                
                if (success) {
                    // Wait for final response
                    String finalResponse = dis.readUTF();
                    success = "SUCCESS".equals(finalResponse);
                    
                    if (success) {
                        dashboard.addLog("File sent successfully: " + file.getName());
                    } else {
                        dashboard.addLog("Peer reported transfer failure: " + finalResponse);
                    }
                }
                
                dashboard.onTransferComplete(transfer, success);
                
            }
            
        } catch (ConnectException e) {
            dashboard.addLog("Connection failed: Peer not available at " + peerIp + ":" + peerPort);
            dashboard.onTransferComplete(transfer, false);
        } catch (SocketTimeoutException e) {
            dashboard.addLog("Transfer timeout: " + e.getMessage());
            dashboard.onTransferComplete(transfer, false);
        } catch (IOException e) {
            dashboard.addLog("Transfer error: " + e.getMessage());
            dashboard.onTransferComplete(transfer, false);
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    dashboard.addLog("Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    private boolean sendFileData(DataOutputStream dos, File file, Transfer transfer) {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            
            byte[] buffer = new byte[BUFFER_SIZE];
            long totalBytesSent = 0;
            long fileSize = file.length();
            int lastProgress = 0;
            
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
                totalBytesSent += bytesRead;
                
                // Update progress
                int progress = (int) ((totalBytesSent * 100) / fileSize);
                if (progress != lastProgress) {
                    lastProgress = progress;
                    dashboard.onTransferProgress(progress, 
                        "Sending: " + progress + "% (" + 
                        FileUtils.formatFileSize(totalBytesSent) + "/" + 
                        FileUtils.formatFileSize(fileSize) + ")");
                }
                
                // Small delay to prevent overwhelming the network
                if (totalBytesSent % (BUFFER_SIZE * 10) == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }
            }
            
            dos.flush();
            dashboard.addLog("File data sent: " + FileUtils.formatFileSize(totalBytesSent) + " bytes");
            return true;
            
        } catch (IOException e) {
            dashboard.addLog("Error sending file data: " + e.getMessage());
            return false;
        }
    }
}
