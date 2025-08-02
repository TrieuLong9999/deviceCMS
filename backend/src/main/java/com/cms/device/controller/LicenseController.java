package com.cms.device.controller;

import com.cms.device.dto.LicenseDto;
import com.cms.device.service.LicenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/licenses")
@Tag(name = "License Management", description = "APIs for managing licenses")
@CrossOrigin(origins = "*")
public class LicenseController {
    
    @Autowired
    private LicenseService licenseService;
    
    @PostMapping
    @Operation(summary = "Create a new license", description = "Creates a new license for a customer")
    public ResponseEntity<LicenseDto> createLicense(@Valid @RequestBody LicenseDto licenseDto) {
        try {
            LicenseDto createdLicense = licenseService.createLicense(licenseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLicense);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{licenseKey}")
    @Operation(summary = "Update license", description = "Updates an existing license")
    public ResponseEntity<LicenseDto> updateLicense(@PathVariable String licenseKey, 
                                                 @Valid @RequestBody LicenseDto licenseDto) {
        try {
            LicenseDto updatedLicense = licenseService.updateLicense(licenseKey, licenseDto);
            return ResponseEntity.ok(updatedLicense);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{licenseKey}")
    @Operation(summary = "Get license by key", description = "Retrieves a license by its key")
    public ResponseEntity<LicenseDto> getLicenseByKey(@PathVariable String licenseKey) {
        try {
            LicenseDto license = licenseService.getLicenseByKey(licenseKey);
            return ResponseEntity.ok(license);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all licenses", description = "Retrieves all licenses")
    public ResponseEntity<List<LicenseDto>> getAllLicenses() {
        List<LicenseDto> licenses = licenseService.getAllLicenses();
        return ResponseEntity.ok(licenses);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get licenses by customer", description = "Retrieves all licenses for a specific customer")
    public ResponseEntity<List<LicenseDto>> getLicensesByCustomer(@PathVariable String customerId) {
        List<LicenseDto> licenses = licenseService.getLicensesByCustomer(customerId);
        return ResponseEntity.ok(licenses);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active licenses", description = "Retrieves all active licenses")
    public ResponseEntity<List<LicenseDto>> getActiveLicenses() {
        List<LicenseDto> licenses = licenseService.getActiveLicenses();
        return ResponseEntity.ok(licenses);
    }
    
    @GetMapping("/type/{licenseType}")
    @Operation(summary = "Get licenses by type", description = "Retrieves all licenses of a specific type")
    public ResponseEntity<List<LicenseDto>> getLicensesByType(@PathVariable String licenseType) {
        List<LicenseDto> licenses = licenseService.getLicensesByType(licenseType);
        return ResponseEntity.ok(licenses);
    }
    
    @DeleteMapping("/{licenseKey}")
    @Operation(summary = "Delete license", description = "Deletes a license")
    public ResponseEntity<Void> deleteLicense(@PathVariable String licenseKey) {
        try {
            licenseService.deleteLicense(licenseKey);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{licenseKey}/activate")
    @Operation(summary = "Activate license", description = "Activates a license")
    public ResponseEntity<Void> activateLicense(@PathVariable String licenseKey) {
        try {
            licenseService.activateLicense(licenseKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{licenseKey}/deactivate")
    @Operation(summary = "Deactivate license", description = "Deactivates a license")
    public ResponseEntity<Void> deactivateLicense(@PathVariable String licenseKey) {
        try {
            licenseService.deactivateLicense(licenseKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{licenseKey}/exists")
    @Operation(summary = "Check license exists", description = "Checks if a license exists")
    public ResponseEntity<Boolean> licenseExists(@PathVariable String licenseKey) {
        boolean exists = licenseService.licenseExists(licenseKey);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/count/active")
    @Operation(summary = "Get active license count", description = "Returns the count of active licenses")
    public ResponseEntity<Long> getActiveLicenseCount() {
        long count = licenseService.getActiveLicenseCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/inactive")
    @Operation(summary = "Get inactive license count", description = "Returns the count of inactive licenses")
    public ResponseEntity<Long> getInactiveLicenseCount() {
        long count = licenseService.getInactiveLicenseCount();
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/update-expired")
    @Operation(summary = "Update expired licenses", description = "Updates all expired licenses to inactive status")
    public ResponseEntity<Integer> updateExpiredLicenses() {
        int updatedCount = licenseService.updateExpiredLicenses();
        return ResponseEntity.ok(updatedCount);
    }
} 