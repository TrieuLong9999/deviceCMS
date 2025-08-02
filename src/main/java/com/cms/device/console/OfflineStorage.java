package com.cms.device.console;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OfflineStorage {
    
    private static final String DATA_DIR = "offline_data";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.json";
    private static final String LICENSES_FILE = DATA_DIR + "/licenses.json";
    private static final String DEVICES_FILE = DATA_DIR + "/devices.json";
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    // Customer operations
    public static boolean saveCustomer(CustomerDto customer) {
        try {
            List<CustomerDto> customers = loadCustomers();
            
            // Remove existing customer with same ID
            customers.removeIf(c -> c.getCustomerId().equals(customer.getCustomerId()));
            
            // Add new customer
            customers.add(customer);
            
            saveCustomers(customers);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }
    
    public static CustomerDto getCustomer(String customerId) {
        try {
            List<CustomerDto> customers = loadCustomers();
            return customers.stream()
                    .filter(c -> c.getCustomerId().equals(customerId))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting customer: " + e.getMessage());
            return null;
        }
    }
    
    public static List<CustomerDto> getAllCustomers() {
        try {
            return loadCustomers();
        } catch (Exception e) {
            System.err.println("Error getting all customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static boolean updateCustomer(String customerId, CustomerDto customer) {
        try {
            List<CustomerDto> customers = loadCustomers();
            
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getCustomerId().equals(customerId)) {
                    customers.set(i, customer);
                    saveCustomers(customers);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteCustomer(String customerId) {
        try {
            List<CustomerDto> customers = loadCustomers();
            boolean removed = customers.removeIf(c -> c.getCustomerId().equals(customerId));
            
            if (removed) {
                saveCustomers(customers);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }
    
    // License operations
    public static boolean saveLicense(LicenseDto license) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            
            // Remove existing license with same key
            licenses.removeIf(l -> l.getLicenseKey().equals(license.getLicenseKey()));
            
            // Add new license
            licenses.add(license);
            
            saveLicenses(licenses);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving license: " + e.getMessage());
            return false;
        }
    }
    
    public static LicenseDto getLicense(String licenseKey) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            return licenses.stream()
                    .filter(l -> l.getLicenseKey().equals(licenseKey))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting license: " + e.getMessage());
            return null;
        }
    }
    
    public static List<LicenseDto> getAllLicenses() {
        try {
            return loadLicenses();
        } catch (Exception e) {
            System.err.println("Error getting all licenses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static List<LicenseDto> getLicensesByCustomer(String customerId) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            return licenses.stream()
                    .filter(l -> l.getCustomerId().equals(customerId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting licenses by customer: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static boolean updateLicense(String licenseKey, LicenseDto license) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            
            for (int i = 0; i < licenses.size(); i++) {
                if (licenses.get(i).getLicenseKey().equals(licenseKey)) {
                    licenses.set(i, license);
                    saveLicenses(licenses);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating license: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteLicense(String licenseKey) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            boolean removed = licenses.removeIf(l -> l.getLicenseKey().equals(licenseKey));
            
            if (removed) {
                saveLicenses(licenses);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting license: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean activateLicense(String licenseKey) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            
            for (LicenseDto license : licenses) {
                if (license.getLicenseKey().equals(licenseKey)) {
                    license.setActive(true);
                    license.setLicenseType("Đã kích hoạt");
                    saveLicenses(licenses);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error activating license: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deactivateLicense(String licenseKey) {
        try {
            List<LicenseDto> licenses = loadLicenses();
            
            for (LicenseDto license : licenses) {
                if (license.getLicenseKey().equals(licenseKey)) {
                    license.setActive(false);
                    license.setLicenseType("Đã vô hiệu hóa");
                    saveLicenses(licenses);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deactivating license: " + e.getMessage());
            return false;
        }
    }
    
    // Device operations
    public static boolean saveDevice(DeviceDto device) {
        try {
            List<DeviceDto> devices = loadDevices();
            
            // Remove existing device with same license and name
            devices.removeIf(d -> d.getLicenseKey().equals(device.getLicenseKey()) && 
                                 d.getDeviceName().equals(device.getDeviceName()));
            
            // Add new device
            devices.add(device);
            
            saveDevices(devices);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving device: " + e.getMessage());
            return false;
        }
    }
    
    public static List<DeviceDto> getDevicesByLicense(String licenseKey) {
        try {
            List<DeviceDto> devices = loadDevices();
            return devices.stream()
                    .filter(d -> d.getLicenseKey().equals(licenseKey))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting devices by license: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static boolean removeDevice(String licenseKey, String deviceName) {
        try {
            List<DeviceDto> devices = loadDevices();
            boolean removed = devices.removeIf(d -> d.getLicenseKey().equals(licenseKey) && 
                                                  d.getDeviceName().equals(deviceName));
            
            if (removed) {
                saveDevices(devices);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error removing device: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean removeAllDevices(String licenseKey) {
        try {
            List<DeviceDto> devices = loadDevices();
            boolean removed = devices.removeIf(d -> d.getLicenseKey().equals(licenseKey));
            
            if (removed) {
                saveDevices(devices);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error removing all devices: " + e.getMessage());
            return false;
        }
    }
    
    public static int getDeviceCount(String licenseKey) {
        try {
            List<DeviceDto> devices = loadDevices();
            return (int) devices.stream()
                    .filter(d -> d.getLicenseKey().equals(licenseKey))
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting device count: " + e.getMessage());
            return 0;
        }
    }
    
    public static boolean deviceExists(String licenseKey, String deviceName) {
        try {
            List<DeviceDto> devices = loadDevices();
            return devices.stream()
                    .anyMatch(d -> d.getLicenseKey().equals(licenseKey) && 
                                 d.getDeviceName().equals(deviceName));
        } catch (Exception e) {
            System.err.println("Error checking device existence: " + e.getMessage());
            return false;
        }
    }
    
    public static List<DeviceDto> getAllDevices() {
        try {
            return loadDevices();
        } catch (Exception e) {
            System.err.println("Error getting all devices: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Helper methods for loading and saving data
    private static List<CustomerDto> loadCustomers() throws IOException {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            return objectMapper.readValue(reader, new TypeReference<List<CustomerDto>>() {});
        }
    }
    
    private static void saveCustomers(List<CustomerDto> customers) throws IOException {
        try (FileWriter writer = new FileWriter(CUSTOMERS_FILE)) {
            objectMapper.writeValue(writer, customers);
        }
    }
    
    private static List<LicenseDto> loadLicenses() throws IOException {
        File file = new File(LICENSES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            return objectMapper.readValue(reader, new TypeReference<List<LicenseDto>>() {});
        }
    }
    
    private static void saveLicenses(List<LicenseDto> licenses) throws IOException {
        try (FileWriter writer = new FileWriter(LICENSES_FILE)) {
            objectMapper.writeValue(writer, licenses);
        }
    }
    
    private static List<DeviceDto> loadDevices() throws IOException {
        File file = new File(DEVICES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            return objectMapper.readValue(reader, new TypeReference<List<DeviceDto>>() {});
        }
    }
    
    private static void saveDevices(List<DeviceDto> devices) throws IOException {
        try (FileWriter writer = new FileWriter(DEVICES_FILE)) {
            objectMapper.writeValue(writer, devices);
        }
    }
} 