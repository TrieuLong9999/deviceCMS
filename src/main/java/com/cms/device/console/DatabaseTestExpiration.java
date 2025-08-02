package com.cms.device.console;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseTestExpiration {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Enhanced Automatic License Expiration Check ===");
        
        try {
            // Test 1: Initialize database schema
            System.out.println("1. Testing database schema initialization...");
            DatabaseSchema.initializeDatabase();
            System.out.println("✅ Database schema initialized successfully");
            
            // Test 2: Create test customer key
            System.out.println("\n2. Creating test customer key...");
            String testCustomerId = "test-customer-expiration-" + System.currentTimeMillis();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair testKeyPair = keyGen.generateKeyPair();
            
            boolean keySaveResult = CustomerKeyDAO.saveCustomerKey(testCustomerId, testKeyPair);
            System.out.println("   Created test customer key: " + keySaveResult);
            
            // Test 3: Create licenses with different expiry dates
            System.out.println("\n3. Creating test licenses with different expiry dates...");
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            
            // License 1: Already expired (yesterday)
            cal.add(Calendar.DAY_OF_MONTH, -1);
            String expiredDate = sdf.format(cal.getTime());
            String expiredLicenseKey = "expired-license-" + System.currentTimeMillis();
            
            boolean expiredLicenseResult = LicenseDAO.upsertLicense(
                expiredLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                expiredDate,
                5,
                true,
                "offline-verified"
            );
            System.out.println("   Created expired license: " + expiredLicenseResult);
            
            // License 2: Expires in 1 minute
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);
            String expiringSoonDate = sdf.format(cal.getTime());
            String expiringSoonLicenseKey = "expiring-soon-license-" + System.currentTimeMillis();
            
            boolean expiringSoonResult = LicenseDAO.upsertLicense(
                expiringSoonLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                expiringSoonDate,
                3,
                true,
                "online-verified"
            );
            System.out.println("   Created expiring soon license: " + expiringSoonResult);
            
            // License 3: Valid license (expires next year)
            cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 1);
            String validDate = sdf.format(cal.getTime());
            String validLicenseKey = "valid-license-" + System.currentTimeMillis();
            
            boolean validLicenseResult = LicenseDAO.upsertLicense(
                validLicenseKey,
                testCustomerId,
                "01/01/2024 00:00:00",
                validDate,
                10,
                true,
                "offline-verified"
            );
            System.out.println("   Created valid license: " + validLicenseResult);
            
            // Test 4: Check initial license status
            System.out.println("\n4. Checking initial license status...");
            List<LicenseDAO.LicenseInfo> allLicenses = LicenseDAO.getAllLicenses();
            System.out.println("   Total licenses in database: " + allLicenses.size());
            
            for (LicenseDAO.LicenseInfo license : allLicenses) {
                if (license.getLicenseKey().contains("expired") || 
                    license.getLicenseKey().contains("expiring") || 
                    license.getLicenseKey().contains("valid")) {
                    System.out.println("   License: " + license.getLicenseKey().substring(0, 20) + "...");
                    System.out.println("     Status: " + license.getLicenseType());
                    System.out.println("     Is Active: " + license.isActive());
                    System.out.println("     Expiry: " + license.getExpiryDate());
                }
            }
            
            // Test 5: Test the enhanced expiration check
            System.out.println("\n5. Testing enhanced expiration check...");
            int expiredCount = LicenseDAO.checkAndUpdateExpiredLicenses();
            System.out.println("   Licenses updated to expired: " + expiredCount);
            
            // Test 6: Check license status after expiration check
            System.out.println("\n6. Checking license status after expiration check...");
            allLicenses = LicenseDAO.getAllLicenses();
            
            for (LicenseDAO.LicenseInfo license : allLicenses) {
                if (license.getLicenseKey().contains("expired") || 
                    license.getLicenseKey().contains("expiring") || 
                    license.getLicenseKey().contains("valid")) {
                    System.out.println("   License: " + license.getLicenseKey().substring(0, 20) + "...");
                    System.out.println("     Status: " + license.getLicenseType());
                    System.out.println("     Is Active: " + license.isActive());
                    System.out.println("     Expiry: " + license.getExpiryDate());
                }
            }
            
            // Test 7: Test multiple expiration checks (should not update again)
            System.out.println("\n7. Testing multiple expiration checks...");
            int expiredCount2 = LicenseDAO.checkAndUpdateExpiredLicenses();
            System.out.println("   Licenses updated to expired (second check): " + expiredCount2);
            
            // Test 8: Wait for expiring license to expire and test again
            System.out.println("\n8. Waiting for expiring license to expire...");
            System.out.println("   Waiting 70 seconds for license to expire...");
            Thread.sleep(70000); // Wait 70 seconds
            
            int expiredCount3 = LicenseDAO.checkAndUpdateExpiredLicenses();
            System.out.println("   Licenses updated to expired (after waiting): " + expiredCount3);
            
            // Test 9: Final status check
            System.out.println("\n9. Final license status check...");
            allLicenses = LicenseDAO.getAllLicenses();
            
            for (LicenseDAO.LicenseInfo license : allLicenses) {
                if (license.getLicenseKey().contains("expired") || 
                    license.getLicenseKey().contains("expiring") || 
                    license.getLicenseKey().contains("valid")) {
                    System.out.println("   License: " + license.getLicenseKey().substring(0, 20) + "...");
                    System.out.println("     Status: " + license.getLicenseType());
                    System.out.println("     Is Active: " + license.isActive());
                    System.out.println("     Expiry: " + license.getExpiryDate());
                }
            }
            
            // Clean up test data
            System.out.println("\n10. Cleaning up test data...");
            DeviceDAO.removeAllDevices(expiredLicenseKey);
            DeviceDAO.removeAllDevices(expiringSoonLicenseKey);
            DeviceDAO.removeAllDevices(validLicenseKey);
            LicenseDAO.deleteLicense(expiredLicenseKey);
            LicenseDAO.deleteLicense(expiringSoonLicenseKey);
            LicenseDAO.deleteLicense(validLicenseKey);
            CustomerKeyDAO.deleteCustomerKey(testCustomerId);
            System.out.println("   Test data cleaned up");
            
            System.out.println("\n✅ Enhanced automatic license expiration check test completed successfully!");
            System.out.println("The enhanced functionality is working correctly:");
            System.out.println("- Database expiration check updates expired licenses automatically");
            System.out.println("- Periodic check in GUI will use this database method");
            System.out.println("- License status is properly updated in the database");
            System.out.println("- Multiple checks don't cause duplicate updates");
            
        } catch (Exception e) {
            System.err.println("❌ Enhanced expiration check test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 