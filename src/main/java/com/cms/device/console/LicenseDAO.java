package com.cms.device.console;

import java.util.ArrayList;
import java.util.List;

public class LicenseDAO {
    
    public static boolean saveLicense(String licenseKey, String customerId, String creationDate, 
                                    String expiryDate, int maxDevices, boolean isActive, String licenseType) {
        try {
            LicenseDto license = new LicenseDto(licenseKey, customerId);
            license.setCreationDate(creationDate);
            license.setExpiryDate(expiryDate);
            license.setMaxDevices(maxDevices);
            license.setActive(isActive);
            license.setLicenseType(licenseType);
            
            return ApiClient.createLicense(license);
        } catch (Exception e) {
            System.err.println("Error saving license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean upsertLicense(String licenseKey, String customerId, String creationDate, 
                                      String expiryDate, int maxDevices, boolean isActive, String licenseType) {
        try {
            LicenseDto existingLicense = ApiClient.getLicense(licenseKey);
            if (existingLicense != null) {
                // Update existing license
                existingLicense.setCustomerId(customerId);
                existingLicense.setCreationDate(creationDate);
                existingLicense.setExpiryDate(expiryDate);
                existingLicense.setMaxDevices(maxDevices);
                existingLicense.setActive(isActive);
                existingLicense.setLicenseType(licenseType);
                
                return ApiClient.updateLicense(licenseKey, existingLicense);
            } else {
                // Create new license
                return saveLicense(licenseKey, customerId, creationDate, expiryDate, maxDevices, isActive, licenseType);
            }
        } catch (Exception e) {
            System.err.println("Error upserting license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateLicenseStatus(String licenseKey, boolean isActive, String licenseType) {
        try {
            LicenseDto license = ApiClient.getLicense(licenseKey);
            if (license != null) {
                license.setActive(isActive);
                license.setLicenseType(licenseType);
                return ApiClient.updateLicense(licenseKey, license);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating license status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check and update expired licenses
     * @return Number of licenses that were updated to expired status
     */
    public static int checkAndUpdateExpiredLicenses() {
        try {
            // This would be handled by the backend scheduled task
            // For now, we'll just return 0 as the backend handles this automatically
            return 0;
        } catch (Exception e) {
            System.err.println("Error checking and updating expired licenses: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    public static boolean deleteLicense(String licenseKey) {
        try {
            return ApiClient.deleteLicense(licenseKey);
        } catch (Exception e) {
            System.err.println("Error deleting license: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean licenseExists(String licenseKey) {
        try {
            LicenseDto license = ApiClient.getLicense(licenseKey);
            return license != null;
        } catch (Exception e) {
            System.err.println("Error checking license existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static LicenseInfo getLicenseInfo(String licenseKey) {
        try {
            LicenseDto license = ApiClient.getLicense(licenseKey);
            if (license != null) {
                return new LicenseInfo(
                    license.getLicenseKey(),
                    license.getCustomerId(),
                    license.getCreationDate(),
                    license.getExpiryDate(),
                    license.getMaxDevices(),
                    license.isActive(),
                    license.getLicenseType()
                );
            }
        } catch (Exception e) {
            System.err.println("Error getting license info: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static List<LicenseInfo> getAllLicenses() {
        try {
            List<LicenseDto> licenses = ApiClient.getAllLicenses();
            List<LicenseInfo> licenseInfos = new ArrayList<>();
            
            for (LicenseDto license : licenses) {
                licenseInfos.add(new LicenseInfo(
                    license.getLicenseKey(),
                    license.getCustomerId(),
                    license.getCreationDate(),
                    license.getExpiryDate(),
                    license.getMaxDevices(),
                    license.isActive(),
                    license.getLicenseType()
                ));
            }
            
            return licenseInfos;
        } catch (Exception e) {
            System.err.println("Error getting all licenses: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static List<LicenseInfo> getLicensesByCustomer(String customerId) {
        try {
            List<LicenseDto> licenses = ApiClient.getLicensesByCustomer(customerId);
            List<LicenseInfo> licenseInfos = new ArrayList<>();
            
            for (LicenseDto license : licenses) {
                licenseInfos.add(new LicenseInfo(
                    license.getLicenseKey(),
                    license.getCustomerId(),
                    license.getCreationDate(),
                    license.getExpiryDate(),
                    license.getMaxDevices(),
                    license.isActive(),
                    license.getLicenseType()
                ));
            }
            
            return licenseInfos;
        } catch (Exception e) {
            System.err.println("Error getting licenses by customer: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
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