package com.cms.device.console;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomerKeyDAO {
    
    public static Map<String, KeyPair> loadAllCustomerKeys() {
        Map<String, KeyPair> customerKeyPairs = new HashMap<>();
        
        String sql = "SELECT customer_id, public_key, private_key FROM customer_keys";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String publicKeyStr = rs.getString("public_key");
                String privateKeyStr = rs.getString("private_key");
                
                try {
                    PublicKey publicKey = decodePublicKey(publicKeyStr);
                    PrivateKey privateKey = decodePrivateKey(privateKeyStr);
                    KeyPair keyPair = new KeyPair(publicKey, privateKey);
                    customerKeyPairs.put(customerId, keyPair);
                } catch (Exception e) {
                    System.err.println("Error loading key for customer " + customerId + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading customer keys: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customerKeyPairs;
    }
    
    public static boolean saveCustomerKey(String customerId, KeyPair keyPair) {
        String sql = "INSERT INTO customer_keys (customer_id, public_key, private_key) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            pstmt.setString(2, encodePublicKey(keyPair.getPublic()));
            pstmt.setString(3, encodePrivateKey(keyPair.getPrivate()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving customer key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteCustomerKey(String customerId) {
        String sql = "DELETE FROM customer_keys WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean customerKeyExists(String customerId) {
        String sql = "SELECT COUNT(*) FROM customer_keys WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking customer key existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static String encodePublicKey(PublicKey publicKey) {
        return java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    
    private static String encodePrivateKey(PrivateKey privateKey) {
        return java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    
    private static PublicKey decodePublicKey(String encodedKey) throws Exception {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(encodedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    
    private static PrivateKey decodePrivateKey(String encodedKey) throws Exception {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(encodedKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
} 