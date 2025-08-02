package com.cms.device.console;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final int TIMEOUT = 5000; // 5 seconds
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    private static boolean isOnline = true;
    
    public static boolean isOnline() {
        return isOnline;
    }
    
    public static void setOnline(boolean online) {
        isOnline = online;
    }
    
    // Customer APIs
    public static boolean createCustomer(CustomerDto customer) {
        if (!testConnection()) {
            return OfflineStorage.saveCustomer(customer);
        }
        
        try {
            String response = makeRequest("POST", "/customers", objectMapper.writeValueAsString(customer));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return OfflineStorage.saveCustomer(customer);
        }
    }
    
    public static CustomerDto getCustomer(String customerId) {
        if (!testConnection()) {
            return OfflineStorage.getCustomer(customerId);
        }
        
        try {
            String response = makeRequest("GET", "/customers/" + customerId, null);
            return response != null ? objectMapper.readValue(response, CustomerDto.class) : null;
        } catch (Exception e) {
            System.err.println("Error getting customer: " + e.getMessage());
            return OfflineStorage.getCustomer(customerId);
        }
    }
    
    public static List<CustomerDto> getAllCustomers() {
        if (!testConnection()) {
            return OfflineStorage.getAllCustomers();
        }
        
        try {
            String response = makeRequest("GET", "/customers", null);
            return response != null ? objectMapper.readValue(response, new TypeReference<List<CustomerDto>>() {}) : null;
        } catch (Exception e) {
            System.err.println("Error getting all customers: " + e.getMessage());
            return OfflineStorage.getAllCustomers();
        }
    }
    
    public static boolean updateCustomer(String customerId, CustomerDto customer) {
        if (!testConnection()) {
            return OfflineStorage.updateCustomer(customerId, customer);
        }
        
        try {
            String response = makeRequest("PUT", "/customers/" + customerId, objectMapper.writeValueAsString(customer));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return OfflineStorage.updateCustomer(customerId, customer);
        }
    }
    
    public static boolean deleteCustomer(String customerId) {
        if (!testConnection()) {
            return OfflineStorage.deleteCustomer(customerId);
        }
        
        try {
            String response = makeRequest("DELETE", "/customers/" + customerId, null);
            return response != null;
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return OfflineStorage.deleteCustomer(customerId);
        }
    }
    
    // License APIs
    public static boolean createLicense(LicenseDto license) {
        if (!testConnection()) {
            return OfflineStorage.saveLicense(license);
        }
        
        try {
            String response = makeRequest("POST", "/licenses", objectMapper.writeValueAsString(license));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error creating license: " + e.getMessage());
            return OfflineStorage.saveLicense(license);
        }
    }
    
    public static LicenseDto getLicense(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.getLicense(licenseKey);
        }
        
        try {
            System.out.println("Getting license: " + licenseKey);
            LicenseDto licenseDto = new LicenseDto();
            licenseDto.setLicenseKey(licenseKey);
            String response = makeRequest("POST", "/licenses/get" , objectMapper.writeValueAsString(licenseDto));
            LicenseDto result = response != null ? objectMapper.readValue(response, LicenseDto.class) : null;
            System.out.println("Get license result: " + (result != null ? "found" : "not found"));
            return result;
        } catch (Exception e) {
            System.err.println("Error getting license: " + e.getMessage());
            return OfflineStorage.getLicense(licenseKey);
        }
    }
    
    public static List<LicenseDto> getAllLicenses() {
        if (!testConnection()) {
            return OfflineStorage.getAllLicenses();
        }
        
        try {
            System.out.println("Getting all licenses");
            String response = makeRequest("GET", "/licenses", null);
            List<LicenseDto> result = response != null ? objectMapper.readValue(response, new TypeReference<List<LicenseDto>>() {}) : null;
            System.out.println("Get all licenses result: " + (result != null ? result.size() + " licenses" : "null"));
            return result;
        } catch (Exception e) {
            System.err.println("Error getting all licenses: " + e.getMessage());
            return OfflineStorage.getAllLicenses();
        }
    }
    
    public static List<LicenseDto> getLicensesByCustomer(String customerId) {
        if (!testConnection()) {
            return OfflineStorage.getLicensesByCustomer(customerId);
        }
        
        try {
            String response = makeRequest("GET", "/licenses/customer/" + customerId, null);
            return response != null ? objectMapper.readValue(response, new TypeReference<List<LicenseDto>>() {}) : null;
        } catch (Exception e) {
            System.err.println("Error getting licenses by customer: " + e.getMessage());
            return OfflineStorage.getLicensesByCustomer(customerId);
        }
    }
    
    public static boolean updateLicense(String licenseKey, LicenseDto license) {
        if (!testConnection()) {
            return OfflineStorage.updateLicense(licenseKey, license);
        }
        
        try {
            System.out.println("Updating license: " + licenseKey + " with data: " + objectMapper.writeValueAsString(license));
            String response = makeRequest("PUT", "/licenses/", objectMapper.writeValueAsString(license));
            boolean result = response != null;
            System.out.println("Update license result: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error updating license: " + e.getMessage());
            return OfflineStorage.updateLicense(licenseKey, license);
        }
    }
    
    public static boolean deleteLicense(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.deleteLicense(licenseKey);
        }
        
        try {
            LicenseDto license = new LicenseDto();
            license.setLicenseKey(licenseKey);
            String response = makeRequest("POST", "/licenses/delete" , objectMapper.writeValueAsString(license));
            LicenseDto result = response != null ? objectMapper.readValue(response, LicenseDto.class) : null;
            return response != null;
        } catch (Exception e) {
            System.err.println("Error deleting license: " + e.getMessage());
            return OfflineStorage.deleteLicense(licenseKey);
        }
    }
    
    public static boolean activateLicense(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.activateLicense(licenseKey);
        }
        
        try {
            String response = makeRequest("PUT", "/licenses/" + licenseKey + "/activate", null);
            return response != null;
        } catch (Exception e) {
            System.err.println("Error activating license: " + e.getMessage());
            return OfflineStorage.activateLicense(licenseKey);
        }
    }
    
    public static boolean deactivateLicense(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.deactivateLicense(licenseKey);
        }
        
        try {
            String response = makeRequest("PUT", "/licenses/" + licenseKey + "/deactivate", null);
            return response != null;
        } catch (Exception e) {
            System.err.println("Error deactivating license: " + e.getMessage());
            return OfflineStorage.deactivateLicense(licenseKey);
        }
    }
    
    // Device APIs
    public static boolean addDevice(DeviceDto device) {
        if (!testConnection()) {
            return OfflineStorage.saveDevice(device);
        }
        
        try {
            String response = makeRequest("POST", "/devices", objectMapper.writeValueAsString(device));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error adding device: " + e.getMessage());
            return OfflineStorage.saveDevice(device);
        }
    }
    
    public static List<DeviceDto> getDevicesByLicense(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.getDevicesByLicense(licenseKey);
        }
        
        try {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setLicenseKey(licenseKey);
            String response = makeRequest("POST", "/devices/license" , objectMapper.writeValueAsString(deviceDto));
            return response != null ? objectMapper.readValue(response, new TypeReference<List<DeviceDto>>() {}) : null;
        } catch (Exception e) {
            System.err.println("Error getting devices by license: " + e.getMessage());
            return OfflineStorage.getDevicesByLicense(licenseKey);
        }
    }
    
    public static List<DeviceDto> getDevicesForLicense(String licenseKey) {
        return getDevicesByLicense(licenseKey);
    }
    
    public static boolean removeDevice(String licenseKey, String deviceName) {
        if (!testConnection()) {
            return OfflineStorage.removeDevice(licenseKey, deviceName);
        }
        
        try {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setLicenseKey(licenseKey);
            String response = makeRequest("DELETE", "/devices/license/name/" + deviceName, objectMapper.writeValueAsString(deviceDto));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error removing device: " + e.getMessage());
            return OfflineStorage.removeDevice(licenseKey, deviceName);
        }
    }
    
    public static boolean removeAllDevices(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.removeAllDevices(licenseKey);
        }
        
        try {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setLicenseKey(licenseKey);
            String response = makeRequest("POST", "/devices/license/all", objectMapper.writeValueAsString(deviceDto));
            return response != null;
        } catch (Exception e) {
            System.err.println("Error removing all devices: " + e.getMessage());
            return OfflineStorage.removeAllDevices(licenseKey);
        }
    }
    
    public static int getDeviceCount(String licenseKey) {
        if (!testConnection()) {
            return OfflineStorage.getDeviceCount(licenseKey);
        }
        
        try {
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setLicenseKey(licenseKey);
            String response = makeRequest("POST", "/devices/license/count", objectMapper.writeValueAsString(deviceDto));
            return response != null ? Integer.parseInt(response) : 0;
        } catch (Exception e) {
            System.err.println("Error getting device count: " + e.getMessage());
            return OfflineStorage.getDeviceCount(licenseKey);
        }
    }
    
    public static boolean deviceExists(String licenseKey, String deviceName) {
        if (!testConnection()) {
            return OfflineStorage.deviceExists(licenseKey, deviceName);
        }
        
        try {
            String response = makeRequest("GET", "/devices/license/" + licenseKey + "/name/" + deviceName + "/exists", null);
            return response != null ? Boolean.parseBoolean(response) : false;
        } catch (Exception e) {
            System.err.println("Error checking device existence: " + e.getMessage());
            return OfflineStorage.deviceExists(licenseKey, deviceName);
        }
    }
    
    public static List<DeviceDto> getAllDevices() {
        if (!testConnection()) {
            return OfflineStorage.getAllDevices();
        }
        
        try {
            String response = makeRequest("GET", "/devices", null);
            return response != null ? objectMapper.readValue(response, new TypeReference<List<DeviceDto>>() {}) : null;
        } catch (Exception e) {
            System.err.println("Error getting all devices: " + e.getMessage());
            return OfflineStorage.getAllDevices();
        }
    }
    
    // Utility method for making HTTP requests
    private static String makeRequest(String method, String endpoint, String requestBody) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            
            if (requestBody != null) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            int responseCode = connection.getResponseCode();
            System.out.println("API Request: " + method + " " + endpoint + " - Response Code: " + responseCode);
            
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String responseStr = response.toString();
                    System.out.println("API Response: " + responseStr);
                    return responseStr;
                }
            } else {
                // Read error response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.err.println("API Error Response: " + response.toString());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Network error: " + e.getMessage());
            isOnline = false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
    
    // Test connection to backend
    public static boolean testConnection() {
        try {
            String response = makeRequest("GET", "/customers/online", null);
            isOnline = response != null;
            return isOnline;
        } catch (Exception e) {
            isOnline = false;
            return false;
        }
    }
} 