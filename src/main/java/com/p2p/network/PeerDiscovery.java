package com.p2p.network;

import com.p2p.database.DatabaseManager;
import com.p2p.model.User;
import java.util.List;
import java.util.ArrayList;

/**
 * Utility class for discovering online peers
 */
public class PeerDiscovery {
    
    /**
     * Get list of online users from database
     */
    public static List<User> getOnlinePeers() {
        return DatabaseManager.getInstance().getOnlineUsers();
    }
    
    /**
     * Get list of available peers with their connection info
     */
    public static List<PeerInfo> getAvailablePeers() {
        List<User> onlineUsers = getOnlinePeers();
        List<PeerInfo> peers = new ArrayList<>();
        
        for (User user : onlineUsers) {
            if (user.getIpAddress() != null && user.getPort() > 0) {
                PeerInfo peerInfo = new PeerInfo(
                    user.getUsername(),
                    user.getIpAddress(),
                    user.getPort(),
                    user.getEmail()
                );
                peers.add(peerInfo);
            }
        }
        
        return peers;
    }
    
    /**
     * Inner class to represent peer information
     */
    public static class PeerInfo {
        private String username;
        private String ipAddress;
        private int port;
        private String email;
        
        public PeerInfo(String username, String ipAddress, int port, String email) {
            this.username = username;
            this.ipAddress = ipAddress;
            this.port = port;
            this.email = email;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getIpAddress() {
            return ipAddress;
        }
        
        public int getPort() {
            return port;
        }
        
        public String getEmail() {
            return email;
        }
        
        @Override
        public String toString() {
            return username + " (" + ipAddress + ":" + port + ")";
        }
    }
}
