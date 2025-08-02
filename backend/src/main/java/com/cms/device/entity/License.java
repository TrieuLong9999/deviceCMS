package com.cms.device.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "licenses")
public class License {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "License key is required")
    @Column(name = "license_key", unique = true, nullable = false, columnDefinition = "TEXT")
    private String licenseKey;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotBlank(message = "Creation date is required")
    @Size(max = 50, message = "Creation date must not exceed 50 characters")
    @Column(name = "creation_date", nullable = false)
    private String creationDate;
    
    @NotBlank(message = "Expiry date is required")
    @Size(max = 50, message = "Expiry date must not exceed 50 characters")
    @Column(name = "expiry_date", nullable = false)
    private String expiryDate;
    
    @NotNull(message = "Max devices is required")
    @Positive(message = "Max devices must be positive")
    @Column(name = "max_devices", nullable = false)
    private Integer maxDevices;
    
    @Column(name = "is_active", nullable = false)
    private boolean active = false;
    
    @Size(max = 50, message = "License type must not exceed 50 characters")
    @Column(name = "license_type", nullable = false)
    private String licenseType = "Chưa kích hoạt";
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();
    
    // Constructors
    public License() {
        this.createdAt = LocalDateTime.now();
    }
    
    public License(String licenseKey, Customer customer) {
        this();
        this.licenseKey = licenseKey;
        this.customer = customer;
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
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    
    public List<Device> getDevices() {
        return devices;
    }
    
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
    
    // Helper methods
    public int getCurrentDeviceCount() {
        return devices != null ? devices.size() : 0;
    }
    
    public boolean canAddDevice() {
        return getCurrentDeviceCount() < maxDevices;
    }
    
    // JPA Lifecycle methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "License{" +
                "id=" + id +
                ", licenseKey='" + licenseKey + '\'' +
                ", customerId='" + (customer != null ? customer.getCustomerId() : null) + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", maxDevices=" + maxDevices +
                ", active=" + active +
                ", licenseType='" + licenseType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 