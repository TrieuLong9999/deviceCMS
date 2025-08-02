package com.cms.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class LicenseDto {
    
    private Long id;
    
    @NotBlank(message = "License key is required")
    private String licenseKey;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Creation date is required")
    @Size(max = 50, message = "Creation date must not exceed 50 characters")
    private String creationDate;
    
    @NotBlank(message = "Expiry date is required")
    @Size(max = 50, message = "Expiry date must not exceed 50 characters")
    private String expiryDate;
    
    @NotNull(message = "Max devices is required")
    @Positive(message = "Max devices must be positive")
    private Integer maxDevices;
    
    private boolean active;
    
    @Size(max = 50, message = "License type must not exceed 50 characters")
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