package com.cms.device.console;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LicenseDAO {
    
    public static boolean saveLicense(String licenseKey, String customerId, String creationDate, 
                                    String expiryDate, int maxDevices, boolean isActive, String licenseType) {
        String sql = "INSERT INTO licenses (license_key, customer_id, creation_date, expiry_date, max_devices, is_active, license_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            pstmt.setString(2, customerId);
            pstmt.setString(3, creationDate);
            pstmt.setString(4, expiryDate);
            pstmt.setInt(5, maxDevices);
            pstmt.setBoolean(6, isActive);
            pstmt.setString(7, licenseType);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean upsertLicense(String licenseKey, String customerId, String creationDate, 
                                      String expiryDate, int maxDevices, boolean isActive, String licenseType) {
        // First check if license exists
        if (licenseExists(licenseKey)) {
            // Update existing license
            String sql = "UPDATE licenses SET customer_id = ?, creation_date = ?, expiry_date = ?, " +
                        "max_devices = ?, is_active = ?, license_type = ? WHERE license_key = ?";
            
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, customerId);
                pstmt.setString(2, creationDate);
                pstmt.setString(3, expiryDate);
                pstmt.setInt(4, maxDevices);
                pstmt.setBoolean(5, isActive);
                pstmt.setString(6, licenseType);
                pstmt.setString(7, licenseKey);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error updating existing license: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            // Insert new license
            return saveLicense(licenseKey, customerId, creationDate, expiryDate, maxDevices, isActive, licenseType);
        }
    }
    
    public static boolean updateLicenseStatus(String licenseKey, boolean isActive, String licenseType) {
        String sql = "UPDATE licenses SET is_active = ?, license_type = ? WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBoolean(1, isActive);
            pstmt.setString(2, licenseType);
            pstmt.setString(3, licenseKey);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating license status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check and update expired licenses in the database
     * @return Number of licenses that were updated to expired status
     */
    public static int checkAndUpdateExpiredLicenses() {
        String sql = "UPDATE licenses SET is_active = false, license_type = 'Hết hạn' " +
                    "WHERE expiry_date IS NOT NULL AND expiry_date != '' " +
                    "AND is_active = true " +
                    "AND license_type NOT IN ('Chưa kích hoạt', 'Hết hạn') " +
                    "AND TO_TIMESTAMP(expiry_date, 'DD/MM/YYYY HH24:MI:SS') < NOW()";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int updatedCount = pstmt.executeUpdate();
            if (updatedCount > 0) {
                System.out.println("Database: Updated " + updatedCount + " expired licenses");
            }
            return updatedCount;
        } catch (SQLException e) {
            System.err.println("Error checking and updating expired licenses: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    public static boolean deleteLicense(String licenseKey) {
        String sql = "DELETE FROM licenses WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean licenseExists(String licenseKey) {
        String sql = "SELECT COUNT(*) FROM licenses WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking license existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static LicenseInfo getLicenseInfo(String licenseKey) {
        String sql = "SELECT license_key, customer_id, creation_date, expiry_date, max_devices, is_active, license_type " +
                    "FROM licenses WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new LicenseInfo(
                        rs.getString("license_key"),
                        rs.getString("customer_id"),
                        rs.getString("creation_date"),
                        rs.getString("expiry_date"),
                        rs.getInt("max_devices"),
                        rs.getBoolean("is_active"),
                        rs.getString("license_type")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting license info: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<LicenseInfo> getAllLicenses() {
        List<LicenseInfo> licenses = new ArrayList<>();
        String sql = "SELECT license_key, customer_id, creation_date, expiry_date, max_devices, is_active, license_type " +
                    "FROM licenses ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                licenses.add(new LicenseInfo(
                    rs.getString("license_key"),
                    rs.getString("customer_id"),
                    rs.getString("creation_date"),
                    rs.getString("expiry_date"),
                    rs.getInt("max_devices"),
                    rs.getBoolean("is_active"),
                    rs.getString("license_type")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all licenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return licenses;
    }
    
    public static List<LicenseInfo> getLicensesByCustomer(String customerId) {
        List<LicenseInfo> licenses = new ArrayList<>();
        String sql = "SELECT license_key, customer_id, creation_date, expiry_date, max_devices, is_active, license_type " +
                    "FROM licenses WHERE customer_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    licenses.add(new LicenseInfo(
                        rs.getString("license_key"),
                        rs.getString("customer_id"),
                        rs.getString("creation_date"),
                        rs.getString("expiry_date"),
                        rs.getInt("max_devices"),
                        rs.getBoolean("is_active"),
                        rs.getString("license_type")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting licenses by customer: " + e.getMessage());
            e.printStackTrace();
        }
        
        return licenses;
    }
    
    public static class LicenseInfo {
        private String licenseKey;
        private String customerId;
        private String creationDate;
        private String expiryDate;
        private int maxDevices;
        private boolean isActive;
        private String licenseType;
        
        public LicenseInfo(String licenseKey, String customerId, String creationDate, 
                          String expiryDate, int maxDevices, boolean isActive, String licenseType) {
            this.licenseKey = licenseKey;
            this.customerId = customerId;
            this.creationDate = creationDate;
            this.expiryDate = expiryDate;
            this.maxDevices = maxDevices;
            this.isActive = isActive;
            this.licenseType = licenseType;
        }
        
        // Getters
        public String getLicenseKey() { return licenseKey; }
        public String getCustomerId() { return customerId; }
        public String getCreationDate() { return creationDate; }
        public String getExpiryDate() { return expiryDate; }
        public int getMaxDevices() { return maxDevices; }
        public boolean isActive() { return isActive; }
        public String getLicenseType() { return licenseType; }
        
        // Setters
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
        public void setMaxDevices(int maxDevices) { this.maxDevices = maxDevices; }
        public void setActive(boolean active) { isActive = active; }
        public void setLicenseType(String licenseType) { this.licenseType = licenseType; }
    }
} 