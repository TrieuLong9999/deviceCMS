package com.cms.device.console;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeviceDAO {
    
    public static boolean addDevice(String licenseKey, String deviceName) {
        String sql = "INSERT INTO license_devices (license_key, device_name) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            pstmt.setString(2, deviceName);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding device: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeDevice(String licenseKey, String deviceName) {
        String sql = "DELETE FROM license_devices WHERE license_key = ? AND device_name = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            pstmt.setString(2, deviceName);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing device: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeAllDevices(String licenseKey) {
        String sql = "DELETE FROM license_devices WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing all devices: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<String> getDevicesForLicense(String licenseKey) {
        List<String> devices = new ArrayList<>();
        String sql = "SELECT device_name FROM license_devices WHERE license_key = ? ORDER BY created_at";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    devices.add(rs.getString("device_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting devices for license: " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    public static int getDeviceCount(String licenseKey) {
        String sql = "SELECT COUNT(*) FROM license_devices WHERE license_key = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting device count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public static boolean deviceExists(String licenseKey, String deviceName) {
        String sql = "SELECT COUNT(*) FROM license_devices WHERE license_key = ? AND device_name = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, licenseKey);
            pstmt.setString(2, deviceName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking device existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public static List<DeviceInfo> getAllDevices() {
        List<DeviceInfo> devices = new ArrayList<>();
        String sql = "SELECT license_key, device_name, created_at FROM license_devices ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                devices.add(new DeviceInfo(
                    rs.getString("license_key"),
                    rs.getString("device_name"),
                    rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all devices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return devices;
    }
    
    public static class DeviceInfo {
        private String licenseKey;
        private String deviceName;
        private java.sql.Timestamp createdAt;
        
        public DeviceInfo(String licenseKey, String deviceName, java.sql.Timestamp createdAt) {
            this.licenseKey = licenseKey;
            this.deviceName = deviceName;
            this.createdAt = createdAt;
        }
        
        // Getters
        public String getLicenseKey() { return licenseKey; }
        public String getDeviceName() { return deviceName; }
        public java.sql.Timestamp getCreatedAt() { return createdAt; }
        
        // Setters
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
    }
} 