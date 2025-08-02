package com.cms.device.console;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DeviceDAO {
    
    public static boolean addDevice(String licenseKey, String deviceName) {
        try {
            DeviceDto device = new DeviceDto(deviceName, licenseKey);
            device.setActive(true);
            device.setCreatedAt(LocalDateTime.now());
            
            return ApiClient.addDevice(device);
        } catch (Exception e) {
            System.err.println("Error adding device: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeDevice(String licenseKey, String deviceName) {
        try {
            return ApiClient.removeDevice(licenseKey, deviceName);
        } catch (Exception e) {
            System.err.println("Error removing device: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeAllDevices(String licenseKey) {
        try {
            return ApiClient.removeAllDevices(licenseKey);
        } catch (Exception e) {
            System.err.println("Error removing all devices: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<String> getDevicesForLicense(String licenseKey) {
        try {
            List<DeviceDto> devices = ApiClient.getDevicesByLicense(licenseKey);
            List<String> deviceNames = new ArrayList<>();
            
            for (DeviceDto device : devices) {
                deviceNames.add(device.getDeviceName());
            }
            
            return deviceNames;
        } catch (Exception e) {
            System.err.println("Error getting devices for license: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static int getDeviceCount(String licenseKey) {
        try {
            return ApiClient.getDeviceCount(licenseKey);
        } catch (Exception e) {
            System.err.println("Error getting device count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    public static boolean deviceExists(String licenseKey, String deviceName) {
        try {
            return ApiClient.deviceExists(licenseKey, deviceName);
        } catch (Exception e) {
            System.err.println("Error checking device existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<DeviceInfo> getAllDevices() {
        try {
            List<DeviceDto> devices = ApiClient.getAllDevices();
            List<DeviceInfo> deviceInfos = new ArrayList<>();
            
            for (DeviceDto device : devices) {
                deviceInfos.add(new DeviceInfo(
                    device.getLicenseKey(),
                    device.getDeviceName(),
                    device.getCreatedAt() != null ? Timestamp.valueOf(device.getCreatedAt()) : null
                ));
            }
            
            return deviceInfos;
        } catch (Exception e) {
            System.err.println("Error getting all devices: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static class DeviceInfo {
        private String licenseKey;
        private String deviceName;
        private Timestamp createdAt;
        
        public DeviceInfo(String licenseKey, String deviceName, Timestamp createdAt) {
            this.licenseKey = licenseKey;
            this.deviceName = deviceName;
            this.createdAt = createdAt;
        }
        
        // Getters
        public String getLicenseKey() { return licenseKey; }
        public String getDeviceName() { return deviceName; }
        public Timestamp getCreatedAt() { return createdAt; }
        
        // Setters
        public void setLicenseKey(String licenseKey) { this.licenseKey = licenseKey; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
} 