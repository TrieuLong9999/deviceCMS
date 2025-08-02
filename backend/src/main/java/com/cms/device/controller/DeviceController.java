package com.cms.device.controller;

import com.cms.device.dto.DeviceDto;
import com.cms.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Device Management", description = "APIs for managing devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    @Autowired
    private DeviceService deviceService;
    
    @PostMapping
    @Operation(summary = "Add a new device", description = "Adds a new device to a license")
    public ResponseEntity<DeviceDto> addDevice(@Valid @RequestBody DeviceDto deviceDto) {
        try {
            DeviceDto addedDevice = deviceService.addDevice(deviceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedDevice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{deviceId}")
    @Operation(summary = "Update device", description = "Updates an existing device")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable Long deviceId, 
                                               @Valid @RequestBody DeviceDto deviceDto) {
        try {
            DeviceDto updatedDevice = deviceService.updateDevice(deviceId, deviceDto);
            return ResponseEntity.ok(updatedDevice);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device by ID", description = "Retrieves a device by its ID")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long deviceId) {
        try {
            DeviceDto device = deviceService.getDeviceById(deviceId);
            return ResponseEntity.ok(device);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/license/{licenseKey}")
    @Operation(summary = "Get devices by license", description = "Retrieves all devices for a specific license")
    public ResponseEntity<List<DeviceDto>> getDevicesByLicense(@PathVariable String licenseKey) {
        List<DeviceDto> devices = deviceService.getDevicesByLicense(licenseKey);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/license/{licenseKey}/active")
    @Operation(summary = "Get active devices by license", description = "Retrieves all active devices for a specific license")
    public ResponseEntity<List<DeviceDto>> getActiveDevicesByLicense(@PathVariable String licenseKey) {
        List<DeviceDto> devices = deviceService.getActiveDevicesByLicense(licenseKey);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieves all devices")
    public ResponseEntity<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active devices", description = "Retrieves all active devices")
    public ResponseEntity<List<DeviceDto>> getActiveDevices() {
        List<DeviceDto> devices = deviceService.getActiveDevices();
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get devices by customer", description = "Retrieves all devices for a specific customer")
    public ResponseEntity<List<DeviceDto>> getDevicesByCustomer(@PathVariable String customerId) {
        List<DeviceDto> devices = deviceService.getDevicesByCustomer(customerId);
        return ResponseEntity.ok(devices);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search devices", description = "Searches devices by name or ID")
    public ResponseEntity<List<DeviceDto>> searchDevices(@RequestParam String term) {
        List<DeviceDto> devices = deviceService.searchDevices(term);
        return ResponseEntity.ok(devices);
    }
    
    @DeleteMapping("/{deviceId}")
    @Operation(summary = "Remove device", description = "Removes a device")
    public ResponseEntity<Void> removeDevice(@PathVariable Long deviceId) {
        try {
            deviceService.removeDevice(deviceId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/license/{licenseKey}/name/{deviceName}")
    @Operation(summary = "Remove device by license and name", description = "Removes a device by license key and device name")
    public ResponseEntity<Void> removeDeviceByLicenseAndName(@PathVariable String licenseKey, 
                                                           @PathVariable String deviceName) {
        try {
            deviceService.removeDeviceByLicenseAndName(licenseKey, deviceName);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/license/{licenseKey}/all")
    @Operation(summary = "Remove all devices by license", description = "Removes all devices for a specific license")
    public ResponseEntity<Void> removeAllDevicesByLicense(@PathVariable String licenseKey) {
        try {
            deviceService.removeAllDevicesByLicense(licenseKey);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{deviceId}/deactivate")
    @Operation(summary = "Deactivate device", description = "Deactivates a device")
    public ResponseEntity<Void> deactivateDevice(@PathVariable Long deviceId) {
        try {
            deviceService.deactivateDevice(deviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{deviceId}/activity")
    @Operation(summary = "Update device activity", description = "Updates the last activity timestamp for a device")
    public ResponseEntity<Void> updateDeviceActivity(@PathVariable Long deviceId) {
        try {
            deviceService.updateDeviceActivity(deviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/license/{licenseKey}/count")
    @Operation(summary = "Get device count by license", description = "Returns the count of devices for a specific license")
    public ResponseEntity<Long> getDeviceCountByLicense(@PathVariable String licenseKey) {
        long count = deviceService.getDeviceCountByLicense(licenseKey);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/active")
    @Operation(summary = "Get active device count", description = "Returns the count of active devices")
    public ResponseEntity<Long> getActiveDeviceCount() {
        long count = deviceService.getActiveDeviceCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/license/{licenseKey}/name/{deviceName}/exists")
    @Operation(summary = "Check device exists", description = "Checks if a device exists for a specific license")
    public ResponseEntity<Boolean> deviceExists(@PathVariable String licenseKey, 
                                             @PathVariable String deviceName) {
        boolean exists = deviceService.deviceExists(licenseKey, deviceName);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/inactive")
    @Operation(summary = "Get inactive devices", description = "Retrieves devices that have been inactive since a threshold")
    public ResponseEntity<List<DeviceDto>> getInactiveDevices(@RequestParam String threshold) {
        try {
            LocalDateTime thresholdDateTime = LocalDateTime.parse(threshold);
            List<DeviceDto> devices = deviceService.getInactiveDevices(thresholdDateTime);
            return ResponseEntity.ok(devices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 