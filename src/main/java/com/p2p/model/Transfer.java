package com.p2p.model;

import java.time.LocalDateTime;

/**
 * Transfer model representing a file transfer record
 */
public class Transfer {
    private int transferId;
    private int userId;
    private String fileName;
    private long fileSize;
    private TransferType transferType;
    private String peerUsername;
    private String peerIpAddress;
    private LocalDateTime timestamp;
    private TransferStatus status;
    private String filePath;

    public enum TransferStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public Transfer() {
        this.timestamp = LocalDateTime.now();
        this.status = TransferStatus.PENDING;
    }

    public Transfer(int userId, String fileName, long fileSize, TransferType transferType, String peerUsername) {
        this();
        this.userId = userId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.transferType = transferType;
        this.peerUsername = peerUsername;
    }

    // Getters and Setters
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public String getPeerUsername() {
        return peerUsername;
    }

    public void setPeerUsername(String peerUsername) {
        this.peerUsername = peerUsername;
    }

    public String getPeerIpAddress() {
        return peerIpAddress;
    }

    public void setPeerIpAddress(String peerIpAddress) {
        this.peerIpAddress = peerIpAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + getFormattedFileSize() +
                ", transferType=" + transferType +
                ", peerUsername='" + peerUsername + '\'' +
                ", status=" + status +
                '}';
    }
}
