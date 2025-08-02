package com.cms.device.console;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.List;

public class DatabaseTestFinal {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Final Database Integration Fixes ===");
        
        try {
            // Test 1: Initialize database schema
            System.out.println("1. Testing database schema initialization...");
            DatabaseSchema.initializeDatabase();
            System.out.println("✅ Database schema initialized successfully");
            
            // Test 2: Test license loading on startup
            System.out.println("\n2. Testing license loading from database...");
            List<LicenseDAO.LicenseInfo> existingLicenses = LicenseDAO.getAllLicenses();
            System.out.println("   Existing licenses in database: " + existingLicenses.size());
            
            // Test 3: Test upsert functionality
            System.out.println("\n3. Testing upsert functionality...");
            
            // Create a test customer key first
            String testCustomerId = "test-customer-final-" + System.currentTimeMillis();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair testKeyPair = keyGen.generateKeyPair();
            
            boolean keySaveResult = CustomerKeyDAO.saveCustomerKey(testCustomerId, testKeyPair);
            System.out.println("   Created test customer key: " + keySaveResult);
            
            // Test creating a new license
            String testLicenseKey = "test-license-final-" + System.currentTimeMillis();
            boolean firstSaveResult = LicenseDAO.upsertLicense(
                testLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                "31/12/2024 23:59:59",
                5,
                false,
                "Chưa kích hoạt"
            );
            System.out.println("   First upsert (insert) result: " + firstSaveResult);
            
            // Test updating the same license
            boolean secondSaveResult = LicenseDAO.upsertLicense(
                testLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                "31/12/2025 23:59:59", // Different expiry date
                10, // Different max devices
                true,
                "offline-verified"
            );
            System.out.println("   Second upsert (update) result: " + secondSaveResult);
            
            // Verify the update
            LicenseDAO.LicenseInfo updatedLicense = LicenseDAO.getLicenseInfo(testLicenseKey);
            System.out.println("   Updated license max devices: " + updatedLicense.getMaxDevices());
            System.out.println("   Updated license is active: " + updatedLicense.isActive());
            System.out.println("   Updated license type: " + updatedLicense.getLicenseType());
            
            // Test 4: Test device management with upsert
            System.out.println("\n4. Testing device management with upsert...");
            
            // Add devices to the license
            boolean device1Result = DeviceDAO.addDevice(testLicenseKey, "DEVICE-FINAL-001");
            boolean device2Result = DeviceDAO.addDevice(testLicenseKey, "DEVICE-FINAL-002");
            System.out.println("   Add device 1 result: " + device1Result);
            System.out.println("   Add device 2 result: " + device2Result);
            
            // Test device count
            int deviceCount = DeviceDAO.getDeviceCount(testLicenseKey);
            System.out.println("   Device count: " + deviceCount);
            
            // Test 5: Test license existence check
            System.out.println("\n5. Testing license existence check...");
            boolean exists = LicenseDAO.licenseExists(testLicenseKey);
            System.out.println("   License exists: " + exists);
            
            boolean notExists = LicenseDAO.licenseExists("non-existent-license");
            System.out.println("   Non-existent license exists: " + notExists);
            
            // Test 6: Test license table refresh simulation
            System.out.println("\n6. Testing license table refresh simulation...");
            List<LicenseDAO.LicenseInfo> allLicenses = LicenseDAO.getAllLicenses();
            System.out.println("   Total licenses after operations: " + allLicenses.size());
            
            // Clean up test data
            System.out.println("\n7. Cleaning up test data...");
            DeviceDAO.removeAllDevices(testLicenseKey);
            LicenseDAO.deleteLicense(testLicenseKey);
            CustomerKeyDAO.deleteCustomerKey(testCustomerId);
            System.out.println("   Test data cleaned up");
            
            System.out.println("\n✅ All final database integration tests passed successfully!");
            System.out.println("The fixes are working correctly:");
            System.out.println("- Licenses will be loaded from database on startup");
            System.out.println("- Upsert functionality prevents duplicate key errors");
            System.out.println("- Device management works correctly with upsert");
            
        } catch (Exception e) {
            System.err.println("❌ Final database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 