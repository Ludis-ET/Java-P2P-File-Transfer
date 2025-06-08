package com.p2p.model;

/**
 * Enumeration for transfer types
 */
public enum TransferType {
    SENT("Sent"),
    RECEIVED("Received");

    private final String displayName;

    TransferType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
