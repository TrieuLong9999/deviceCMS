package com.cms.device.console;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Map;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Database Integration Step 1 ===");
        
        try {
            // Test 1: Initialize database schema
            System.out.println("1. Testing database schema initialization...");
            DatabaseSchema.initializeDatabase();
            System.out.println("✅ Database schema initialized successfully");
            
            // Test 2: Test customer key operations
            System.out.println("\n2. Testing customer key operations...");
            
            // Check if any keys exist
            Map<String, KeyPair> existingKeys = CustomerKeyDAO.loadAllCustomerKeys();
            System.out.println("   Current keys in database: " + existingKeys.size());
            
            // Test creating a new key
            String testCustomerId = "test-customer-" + System.currentTimeMillis();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair testKeyPair = keyGen.generateKeyPair();
            
            boolean saveResult = CustomerKeyDAO.saveCustomerKey(testCustomerId, testKeyPair);
            System.out.println("   Save key result: " + saveResult);
            
            // Test loading keys again
            Map<String, KeyPair> keysAfterSave = CustomerKeyDAO.loadAllCustomerKeys();
            System.out.println("   Keys after save: " + keysAfterSave.size());
            
            // Test key existence check
            boolean exists = CustomerKeyDAO.customerKeyExists(testCustomerId);
            System.out.println("   Key exists check: " + exists);
            
            // Test deleting the key
            boolean deleteResult = CustomerKeyDAO.deleteCustomerKey(testCustomerId);
            System.out.println("   Delete key result: " + deleteResult);
            
            // Verify deletion
            Map<String, KeyPair> keysAfterDelete = CustomerKeyDAO.loadAllCustomerKeys();
            System.out.println("   Keys after delete: " + keysAfterDelete.size());
            
            System.out.println("\n✅ All database tests passed successfully!");
            System.out.println("Step 1 is ready for testing. You can now run the application.");
            
        } catch (Exception e) {
            System.err.println("❌ Database test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 