package com.cms.device.service;

import com.cms.device.dto.LicenseDto;
import com.cms.device.entity.Customer;
import com.cms.device.entity.License;
import com.cms.device.repository.CustomerRepository;
import com.cms.device.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicenseService {
    
    @Autowired
    private LicenseRepository licenseRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public LicenseDto createLicense(LicenseDto licenseDto) {
        if (licenseRepository.existsByLicenseKey(licenseDto.getLicenseKey())) {
            throw new RuntimeException("License with key " + licenseDto.getLicenseKey() + " already exists");
        }
        
        Customer customer = customerRepository.findByCustomerId(licenseDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + licenseDto.getCustomerId()));
        
        License license = new License();
        license.setLicenseKey(licenseDto.getLicenseKey());
        license.setCustomer(customer);
        license.setCreationDate(licenseDto.getCreationDate());
        license.setExpiryDate(licenseDto.getExpiryDate());
        license.setMaxDevices(licenseDto.getMaxDevices());
        license.setActive(licenseDto.isActive());
        license.setLicenseType(licenseDto.getLicenseType() != null ? licenseDto.getLicenseType() : "Chưa kích hoạt");
        
        License savedLicense = licenseRepository.save(license);
        return convertToDto(savedLicense);
    }
    
    public LicenseDto updateLicense(String licenseKey, LicenseDto licenseDto) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found with key: " + licenseKey));
        
        if (licenseDto.getCustomerId() != null) {
            Customer customer = customerRepository.findByCustomerId(licenseDto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + licenseDto.getCustomerId()));
            license.setCustomer(customer);
        }
        
        license.setCreationDate(licenseDto.getCreationDate());
        license.setExpiryDate(licenseDto.getExpiryDate());
        license.setMaxDevices(licenseDto.getMaxDevices());
        license.setActive(licenseDto.isActive());
        license.setLicenseType(licenseDto.getLicenseType());
        
        License updatedLicense = licenseRepository.save(license);
        return convertToDto(updatedLicense);
    }
    
    public LicenseDto getLicenseByKey(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found with key: " + licenseKey));
        return convertToDto(license);
    }
    
    public List<LicenseDto> getAllLicenses() {
        return licenseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<LicenseDto> getLicensesByCustomer(String customerId) {
        return licenseRepository.findByCustomerCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<LicenseDto> getActiveLicenses() {
        return licenseRepository.findByActive(true).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<LicenseDto> getLicensesByType(String licenseType) {
        return licenseRepository.findByLicenseType(licenseType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void deleteLicense(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found with key: " + licenseKey));
        licenseRepository.delete(license);
    }
    
    public void activateLicense(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found with key: " + licenseKey));
        license.setActive(true);
        license.setLicenseType("Đã kích hoạt");
        licenseRepository.save(license);
    }
    
    public void deactivateLicense(String licenseKey) {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("License not found with key: " + licenseKey));
        license.setActive(false);
        license.setLicenseType("Đã vô hiệu hóa");
        licenseRepository.save(license);
    }
    
    public boolean licenseExists(String licenseKey) {
        return licenseRepository.existsByLicenseKey(licenseKey);
    }
    
    public long getActiveLicenseCount() {
        return licenseRepository.countActiveLicenses();
    }
    
    public long getInactiveLicenseCount() {
        return licenseRepository.countInactiveLicenses();
    }
    
    @Scheduled(cron = "0 0 1 * * ?") // Run daily at 1 AM
    public void checkAndUpdateExpiredLicenses() {
        int updatedCount = licenseRepository.updateExpiredLicenses();
        if (updatedCount > 0) {
            System.out.println("Updated " + updatedCount + " expired licenses");
        }
    }
    
    public int updateExpiredLicenses() {
        return licenseRepository.updateExpiredLicenses();
    }
    
    private LicenseDto convertToDto(License license) {
        LicenseDto dto = new LicenseDto();
        dto.setId(license.getId());
        dto.setLicenseKey(license.getLicenseKey());
        dto.setCustomerId(license.getCustomer().getCustomerId());
        dto.setCreationDate(license.getCreationDate());
        dto.setExpiryDate(license.getExpiryDate());
        dto.setMaxDevices(license.getMaxDevices());
        dto.setActive(license.isActive());
        dto.setLicenseType(license.getLicenseType());
        dto.setCreatedAt(license.getCreatedAt());
        dto.setUpdatedAt(license.getUpdatedAt());
        dto.setCurrentDeviceCount(license.getCurrentDeviceCount());
        return dto;
    }
} 