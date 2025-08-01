package com.cms.device.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Quản lý việc lưu trữ và đọc dữ liệu từ file JSON
 * Giả lập Backend bằng cách sử dụng file JSON local
 */
public class JSONDataManager {
    
    private static final String DATA_DIR = "data";
    private static final String LICENSES_FILE = DATA_DIR + "/licenses.json";
    private static final String DEVICE_COUNTS_FILE = DATA_DIR + "/device_counts.json";
    private static final String CONFIG_FILE = DATA_DIR + "/config.json";
    private static final String KEY_PAIRS_FILE = DATA_DIR + "/key_pairs.json";
    private static final String API_BASE_URL = "https://api.vhtc.com.vn/license-server/v1";
    
    private final ObjectMapper objectMapper;
    private final Random random = new Random();
    
    public JSONDataManager() {
        this.objectMapper = new ObjectMapper();
        initializeDataDirectory();
    }
    
    /**
     * Khởi tạo thư mục data và các file JSON cơ bản
     */
    private void initializeDataDirectory() {
        try {
            // Tạo thư mục data nếu chưa tồn tại
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("✅ Đã tạo thư mục data: " + dataDir.getAbsolutePath());
            }
            
            // Tạo file licenses.json nếu chưa tồn tại
            File licensesFile = new File(LICENSES_FILE);
            if (!licensesFile.exists()) {
                saveToFile(LICENSES_FILE, new HashMap<String, Map<String, Object>>());
                System.out.println("✅ Đã tạo file " + LICENSES_FILE);
            }
            
            // Tạo file device_counts.json nếu chưa tồn tại
            File deviceCountsFile = new File(DEVICE_COUNTS_FILE);
            if (!deviceCountsFile.exists()) {
                saveToFile(DEVICE_COUNTS_FILE, new HashMap<String, Integer>());
                System.out.println("✅ Đã tạo file " + DEVICE_COUNTS_FILE);
            }
            
            // Tạo file config.json nếu chưa tồn tại
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                Map<String, Object> config = new HashMap<>();
                config.put("activeLicenseKey", null);
                config.put("lastUpdated", System.currentTimeMillis());
                config.put("version", "1.0");
                saveToFile(CONFIG_FILE, config);
                System.out.println("✅ Đã tạo file " + CONFIG_FILE);
            }
            
            // Tạo file key_pairs.json nếu chưa tồn tại
            File keyPairsFile = new File(KEY_PAIRS_FILE);
            if (!keyPairsFile.exists()) {
                saveToFile(KEY_PAIRS_FILE, new HashMap<String, Map<String, String>>());
                System.out.println("✅ Đã tạo file " + KEY_PAIRS_FILE);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khởi tạo thư mục data: " + e.getMessage());
        }
    }
    
    /**
     * Giả lập API call đến server
     */
    private void simulateServerCall(String endpoint, String method, String description) {
        String url = API_BASE_URL + endpoint;
        System.out.println("🌐 " + method + " " + url);
        System.out.println("📡 " + description + "...");
        
        // Loại bỏ hoàn toàn network delay để tăng tốc độ
        // int delay = 200 + random.nextInt(800); // 200-1000ms
        // Thread.sleep(delay);
        
        // Giả lập response ngay lập tức
        int statusCode = 200; // Success
        System.out.println("✅ HTTP " + statusCode + " - Hoàn thành ngay lập tức");
    }
    
    /**
     * Lưu tất cả license data vào file JSON
     */
    public boolean saveLicenses(Map<String, Map<String, Object>> allLicenses) {
        try {
            simulateServerCall("/licenses", "POST", "Đồng bộ " + allLicenses.size() + " licenses lên server");
            saveToFile(LICENSES_FILE, allLicenses);
            System.out.println("💾 Đã lưu và backup local: " + LICENSES_FILE);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi đồng bộ licenses: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đọc tất cả license data từ file JSON
     */
    public Map<String, Map<String, Object>> loadLicenses() {
        try {
            simulateServerCall("/licenses", "GET", "Tải licenses từ server");
            TypeReference<Map<String, Map<String, Object>>> typeRef = new TypeReference<Map<String, Map<String, Object>>>() {};
            Map<String, Map<String, Object>> result = loadFromFile(LICENSES_FILE, typeRef);
            System.out.println("📖 Đã tải " + result.size() + " licenses từ server (cached local)");
            return result;
        } catch (Exception e) {
            System.err.println("❌ Lỗi tải licenses từ server: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Lưu device counts vào file JSON
     */
    public boolean saveDeviceCounts(Map<String, AtomicInteger> deviceCounts) {
        try {
            simulateServerCall("/device-counts", "PUT", "Cập nhật device counts lên server");
            // Convert AtomicInteger to Integer cho JSON
            Map<String, Integer> countsMap = new HashMap<>();
            for (Map.Entry<String, AtomicInteger> entry : deviceCounts.entrySet()) {
                countsMap.put(entry.getKey(), entry.getValue().get());
            }
            saveToFile(DEVICE_COUNTS_FILE, countsMap);
            System.out.println("💾 Đã cập nhật device counts (backup local)");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật device counts: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đọc device counts từ file JSON
     */
    public Map<String, AtomicInteger> loadDeviceCounts() {
        try {
            simulateServerCall("/device-counts", "GET", "Tải device counts từ server");
            TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
            Map<String, Integer> countsMap = loadFromFile(DEVICE_COUNTS_FILE, typeRef);
            
            // Convert Integer to AtomicInteger
            Map<String, AtomicInteger> deviceCounts = new HashMap<>();
            for (Map.Entry<String, Integer> entry : countsMap.entrySet()) {
                deviceCounts.put(entry.getKey(), new AtomicInteger(entry.getValue()));
            }
            System.out.println("📖 Đã tải device counts cho " + deviceCounts.size() + " licenses từ server");
            return deviceCounts;
        } catch (Exception e) {
            System.err.println("❌ Lỗi tải device counts từ server: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Lưu config (active license key, etc.)
     */
    public boolean saveConfig(String activeLicenseKey) {
        try {
            simulateServerCall("/config", "PUT", "Cập nhật cấu hình hệ thống");
            Map<String, Object> config = new HashMap<>();
            config.put("activeLicenseKey", activeLicenseKey);
            config.put("lastUpdated", System.currentTimeMillis());
            config.put("version", "1.0");
            saveToFile(CONFIG_FILE, config);
            System.out.println("💾 Đã cập nhật cấu hình hệ thống (backup local)");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi cập nhật cấu hình: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đọc config từ file JSON
     */
    public String loadActiveLicenseKey() {
        try {
            simulateServerCall("/config", "GET", "Tải cấu hình hệ thống từ server");
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> config = loadFromFile(CONFIG_FILE, typeRef);
            String activeLicenseKey = (String) config.get("activeLicenseKey");
            System.out.println("📖 Đã tải cấu hình - activeLicenseKey: " + activeLicenseKey);
            return activeLicenseKey;
        } catch (Exception e) {
            System.err.println("❌ Lỗi tải cấu hình từ server: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lưu customer key pairs vào file JSON
     */
    public boolean saveKeyPairs(Map<String, KeyPair> customerKeyPairs) {
        return saveKeyPairsWithNames(customerKeyPairs, new HashMap<>());
    }
    
    /**
     * Lưu customer key pairs và tên khách hàng vào file JSON
     */
    public boolean saveKeyPairsWithNames(Map<String, KeyPair> customerKeyPairs, Map<String, String> customerNames) {
        try {
            // Convert KeyPair to String format for JSON storage
            Map<String, Map<String, String>> keyPairsData = new HashMap<>();
            for (Map.Entry<String, KeyPair> entry : customerKeyPairs.entrySet()) {
                String customerId = entry.getKey();
                KeyPair keyPair = entry.getValue();
                
                Map<String, String> keyData = new HashMap<>();
                keyData.put("publicKey", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                keyData.put("privateKey", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
                keyData.put("algorithm", keyPair.getPublic().getAlgorithm());
                
                // Thêm tên khách hàng nếu có
                String customerName = customerNames.get(customerId);
                if (customerName != null && !customerName.trim().isEmpty()) {
                    keyData.put("customerName", customerName);
                }
                
                keyPairsData.put(customerId, keyData);
            }
            
            simulateServerCall("/key-pairs", "POST", "Đồng bộ " + customerKeyPairs.size() + " key pairs lên server");
            saveToFile(KEY_PAIRS_FILE, keyPairsData);
            System.out.println("💾 Đã đồng bộ " + customerKeyPairs.size() + " key pairs (backup local)");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi lưu key pairs: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đọc customer key pairs từ file JSON
     */
    public Map<String, KeyPair> loadKeyPairs() {
        Map<String, KeyPair> keyPairs = new HashMap<>();
        loadKeyPairsWithNames(keyPairs, new HashMap<>());
        return keyPairs;
    }
    
    /**
     * Đọc customer key pairs và tên khách hàng từ file JSON
     */
    public void loadKeyPairsWithNames(Map<String, KeyPair> customerKeyPairs, Map<String, String> customerNames) {
        try {
            simulateServerCall("/key-pairs", "GET", "Tải key pairs từ server");
            TypeReference<Map<String, Map<String, String>>> typeRef = new TypeReference<Map<String, Map<String, String>>>() {};
            Map<String, Map<String, String>> keyPairsData = loadFromFile(KEY_PAIRS_FILE, typeRef);
            
            // Clear existing data
            customerKeyPairs.clear();
            customerNames.clear();
            
            // Convert String format back to KeyPair objects
            for (Map.Entry<String, Map<String, String>> entry : keyPairsData.entrySet()) {
                String customerId = entry.getKey();
                Map<String, String> keyData = entry.getValue();
                
                try {
                    // Decode public key
                    byte[] publicKeyBytes = Base64.getDecoder().decode(keyData.get("publicKey"));
                    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                    KeyFactory keyFactory = KeyFactory.getInstance(keyData.getOrDefault("algorithm", "RSA"));
                    PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
                    
                    // Decode private key
                    byte[] privateKeyBytes = Base64.getDecoder().decode(keyData.get("privateKey"));
                    PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
                    
                    // Create KeyPair
                    KeyPair keyPair = new KeyPair(publicKey, privateKey);
                    customerKeyPairs.put(customerId, keyPair);
                    
                    // Load customer name if available
                    String customerName = keyData.get("customerName");
                    if (customerName != null && !customerName.trim().isEmpty()) {
                        customerNames.put(customerId, customerName);
                    }
                } catch (Exception keyException) {
                    System.err.println("❌ Lỗi load key pair cho customer '" + customerId + "': " + keyException.getMessage());
                }
            }
            
            System.out.println("📖 Đã tải " + customerKeyPairs.size() + " key pairs từ server");
        } catch (Exception e) {
            System.err.println("❌ Lỗi tải key pairs từ server: " + e.getMessage());
        }
    }
    
    /**
     * Backup tất cả dữ liệu với timestamp
     */
    public boolean backupAllData() {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String backupDir = DATA_DIR + "/backup_" + timestamp;
            File backupDirFile = new File(backupDir);
            backupDirFile.mkdirs();
            
            // Copy tất cả file JSON vào thư mục backup
            Files.copy(Paths.get(LICENSES_FILE), Paths.get(backupDir + "/licenses.json"));
            Files.copy(Paths.get(DEVICE_COUNTS_FILE), Paths.get(backupDir + "/device_counts.json"));
            Files.copy(Paths.get(CONFIG_FILE), Paths.get(backupDir + "/config.json"));
            Files.copy(Paths.get(KEY_PAIRS_FILE), Paths.get(backupDir + "/key_pairs.json"));
            
            System.out.println("✅ Đã backup dữ liệu vào " + backupDir);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Lỗi backup dữ liệu: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lưu tự động tất cả dữ liệu
     */
    public boolean autoSaveAll(Map<String, Map<String, Object>> allLicenses, 
                              Map<String, AtomicInteger> deviceCounts, 
                              String activeLicenseKey,
                              Map<String, KeyPair> customerKeyPairs) {
        return autoSaveAllWithNames(allLicenses, deviceCounts, activeLicenseKey, customerKeyPairs, new HashMap<>());
    }
    
    /**
     * Lưu tự động tất cả dữ liệu bao gồm customer names
     */
    public boolean autoSaveAllWithNames(Map<String, Map<String, Object>> allLicenses, 
                                      Map<String, AtomicInteger> deviceCounts, 
                                      String activeLicenseKey,
                                      Map<String, KeyPair> customerKeyPairs,
                                      Map<String, String> customerNames) {
        boolean success = true;
        success &= saveLicenses(allLicenses);
        success &= saveDeviceCounts(deviceCounts);
        success &= saveConfig(activeLicenseKey);
        success &= saveKeyPairsWithNames(customerKeyPairs, customerNames);
        
        if (success) {
            System.out.println("✅ Auto-save hoàn tất!");
        } else {
            System.err.println("❌ Có lỗi trong quá trình auto-save!");
        }
        return success;
    }
    
    /**
     * Lấy thông tin tổng quan về dữ liệu
     */
    public void printDataSummary() {
        try {
            Map<String, Map<String, Object>> licenses = loadLicenses();
            Map<String, AtomicInteger> deviceCounts = loadDeviceCounts();
            String activeLicenseKey = loadActiveLicenseKey();
            Map<String, KeyPair> keyPairs = loadKeyPairs();
            
            System.out.println("\n📊 TỔNG QUAN DỮ LIỆU JSON:");
            System.out.println("─".repeat(50));
            System.out.println("📁 Thư mục dữ liệu: " + new File(DATA_DIR).getAbsolutePath());
            System.out.println("📄 Số licenses: " + licenses.size());
            System.out.println("🔑 Số key pairs: " + keyPairs.size());
            System.out.println("🔢 Tổng device counts: " + deviceCounts.values().stream().mapToInt(AtomicInteger::get).sum());
            System.out.println("🎯 Active license: " + (activeLicenseKey != null ? activeLicenseKey : "Không có"));
            System.out.println("📅 Kích thước file licenses: " + getFileSize(LICENSES_FILE) + " bytes");
            System.out.println("📅 Kích thước file device_counts: " + getFileSize(DEVICE_COUNTS_FILE) + " bytes");
            System.out.println("📅 Kích thước file config: " + getFileSize(CONFIG_FILE) + " bytes");
            System.out.println("📅 Kích thước file key_pairs: " + getFileSize(KEY_PAIRS_FILE) + " bytes");
        } catch (Exception e) {
            System.err.println("❌ Lỗi hiển thị tổng quan dữ liệu: " + e.getMessage());
        }
    }
    
    // Helper methods
    private <T> void saveToFile(String filePath, T data) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
    }
    
    private <T> T loadFromFile(String filePath, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(new File(filePath), typeReference);
    }
    
    private long getFileSize(String filePath) {
        try {
            return new File(filePath).length();
        } catch (Exception e) {
            return 0;
        }
    }
} 