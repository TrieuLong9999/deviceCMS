package com.cms.device.console;

import java.time.LocalDateTime;

public class LicenseDto {
    
    private Long id;
    private String licenseKey;
    private String customerId;
    private String creationDate;
    private String expiryDate;
    private Integer maxDevices;
    private boolean active;
    private String licenseType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int currentDeviceCount;
    
    // Constructors
    public LicenseDto() {}
    
    public LicenseDto(String licenseKey, String customerId) {
        this.licenseKey = licenseKey;
        this.customerId = customerId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLicenseKey() {
        return licenseKey;
    }
    
    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Integer getMaxDevices() {
        return maxDevices;
    }
    
    public void setMaxDevices(Integer maxDevices) {
        this.maxDevices = maxDevices;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getLicenseType() {
        return licenseType;
    }
    
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getCurrentDeviceCount() {
        return currentDeviceCount;
    }
    
    public void setCurrentDeviceCount(int currentDeviceCount) {
        this.currentDeviceCount = currentDeviceCount;
    }
    
    // Helper methods
    public boolean canAddDevice() {
        return currentDeviceCount < maxDevices;
    }
    
    @Override
    public String toString() {
        return "LicenseDto{" +
                "id=" + id +
                ", licenseKey='" + licenseKey + '\'' +
                ", customerId='" + customerId + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", maxDevices=" + maxDevices +
                ", active=" + active +
                ", licenseType='" + licenseType + '\'' +
                ", currentDeviceCount=" + currentDeviceCount +
                ", createdAt=" + createdAt +
                '}';
    }
} 