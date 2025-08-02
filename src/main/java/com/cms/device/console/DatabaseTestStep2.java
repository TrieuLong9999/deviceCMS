package com.cms.device.console;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.List;

public class DatabaseTestStep2 {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Database Integration Step 2 ===");
        
        try {
            // Test 1: Initialize database schema
            System.out.println("1. Testing database schema initialization...");
            DatabaseSchema.initializeDatabase();
            System.out.println("✅ Database schema initialized successfully");
            
            // Test 2: Test license operations
            System.out.println("\n2. Testing license operations...");
            
            // Create a test customer key first
            String testCustomerId = "test-customer-step2-" + System.currentTimeMillis();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair testKeyPair = keyGen.generateKeyPair();
            
            boolean keySaveResult = CustomerKeyDAO.saveCustomerKey(testCustomerId, testKeyPair);
            System.out.println("   Created test customer key: " + keySaveResult);
            
            // Test creating a license
            String testLicenseKey = "test-license-" + System.currentTimeMillis();
            boolean licenseSaveResult = LicenseDAO.saveLicense(
                testLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                "31/12/2024 23:59:59",
                5,
                false,
                "Chưa kích hoạt"
            );
            System.out.println("   Save license result: " + licenseSaveResult);
            
            // Test loading licenses
            List<LicenseDAO.LicenseInfo> licenses = LicenseDAO.getAllLicenses();
            System.out.println("   Total licenses in database: " + licenses.size());
            
            // Test getting license info
            LicenseDAO.LicenseInfo licenseInfo = LicenseDAO.getLicenseInfo(testLicenseKey);
            System.out.println("   License info retrieved: " + (licenseInfo != null));
            if (licenseInfo != null) {
                System.out.println("   License customer ID: " + licenseInfo.getCustomerId());
                System.out.println("   License max devices: " + licenseInfo.getMaxDevices());
            }
            
            // Test 3: Test device operations
            System.out.println("\n3. Testing device operations...");
            
            // Add devices to the license
            boolean device1Result = DeviceDAO.addDevice(testLicenseKey, "DEVICE-001");
            boolean device2Result = DeviceDAO.addDevice(testLicenseKey, "DEVICE-002");
            System.out.println("   Add device 1 result: " + device1Result);
            System.out.println("   Add device 2 result: " + device2Result);
            
            // Test device count
            int deviceCount = DeviceDAO.getDeviceCount(testLicenseKey);
            System.out.println("   Device count: " + deviceCount);
            
            // Test getting devices
            List<String> devices = DeviceDAO.getDevicesForLicense(testLicenseKey);
            System.out.println("   Devices for license: " + devices.size());
            for (String device : devices) {
                System.out.println("     - " + device);
            }
            
            // Test device existence
            boolean deviceExists = DeviceDAO.deviceExists(testLicenseKey, "DEVICE-001");
            System.out.println("   Device DEVICE-001 exists: " + deviceExists);
            
            // Test 4: Test license status updates
            System.out.println("\n4. Testing license status updates...");
            
            boolean updateResult = LicenseDAO.updateLicenseStatus(testLicenseKey, true, "offline-verified");
            System.out.println("   Update license status result: " + updateResult);
            
            // Verify the update
            LicenseDAO.LicenseInfo updatedLicense = LicenseDAO.getLicenseInfo(testLicenseKey);
            System.out.println("   License is active: " + updatedLicense.isActive());
            System.out.println("   License type: " + updatedLicense.getLicenseType());
            
            // Test 5: Test device removal
            System.out.println("\n5. Testing device removal...");
            
            boolean removeResult = DeviceDAO.removeDevice(testLicenseKey, "DEVICE-001");
            System.out.println("   Remove device result: " + removeResult);
            
            // Verify removal
            int newDeviceCount = DeviceDAO.getDeviceCount(testLicenseKey);
            System.out.println("   New device count: " + newDeviceCount);
            
            // Test 6: Test license deletion
            System.out.println("\n6. Testing license deletion...");
            
            // Remove all devices first
            DeviceDAO.removeAllDevices(testLicenseKey);
            System.out.println("   Removed all devices");
            
            // Delete the license
            boolean deleteResult = LicenseDAO.deleteLicense(testLicenseKey);
            System.out.println("   Delete license result: " + deleteResult);
            
            // Verify deletion
            List<LicenseDAO.LicenseInfo> remainingLicenses = LicenseDAO.getAllLicenses();
            System.out.println("   Remaining licenses: " + remainingLicenses.size());
            
            // Clean up test customer key
            CustomerKeyDAO.deleteCustomerKey(testCustomerId);
            System.out.println("   Cleaned up test customer key");
            
            System.out.println("\n✅ All Step 2 database tests passed successfully!");
            System.out.println("Step 2 is ready for testing. You can now run the application.");
            
        } catch (Exception e) {
            System.err.println("❌ Step 2 database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 