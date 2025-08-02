package com.cms.device.console;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerKeyDAO {
    
    public static Map<String, KeyPair> loadAllCustomerKeys() {
        Map<String, KeyPair> customerKeyPairs = new HashMap<>();
        
        try {
            List<CustomerDto> customers = ApiClient.getAllCustomers();
            for (CustomerDto customer : customers) {
                if (customer.getPublicKey() != null && customer.getPrivateKey() != null) {
                    try {
                        PublicKey publicKey = decodePublicKey(customer.getPublicKey());
                        PrivateKey privateKey = decodePrivateKey(customer.getPrivateKey());
                        KeyPair keyPair = new KeyPair(publicKey, privateKey);
                        customerKeyPairs.put(customer.getCustomerId(), keyPair);
                    } catch (Exception e) {
                        System.err.println("Error loading key for customer " + customer.getCustomerId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading customer keys: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customerKeyPairs;
    }
    
    public static boolean saveCustomerKey(String customerId, KeyPair keyPair) {
        try {
            CustomerDto customer = ApiClient.getCustomer(customerId);
            if (customer == null) {
                customer = new CustomerDto(customerId);
            }
            
            customer.setPublicKey(encodePublicKey(keyPair.getPublic()));
            customer.setPrivateKey(encodePrivateKey(keyPair.getPrivate()));
            
            return ApiClient.createCustomer(customer);
        } catch (Exception e) {
            System.err.println("Error saving customer key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteCustomerKey(String customerId) {
        try {
            return ApiClient.deleteCustomer(customerId);
        } catch (Exception e) {
            System.err.println("Error deleting customer key: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean customerKeyExists(String customerId) {
        try {
            CustomerDto customer = ApiClient.getCustomer(customerId);
            return customer != null;
        } catch (Exception e) {
            System.err.println("Error checking customer key existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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