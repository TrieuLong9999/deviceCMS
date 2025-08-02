package com.cms.device.service;

import com.cms.device.dto.DeviceDto;
import com.cms.device.entity.Device;
import com.cms.device.entity.License;
import com.cms.device.repository.DeviceRepository;
import com.cms.device.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private LicenseRepository licenseRepository;
    
    public DeviceDto addDevice(DeviceDto deviceDto) {
        License license = licenseRepository.findByLicenseKey(deviceDto.getLicenseKey())
                .orElseThrow(() -> new RuntimeException("License not found with key: " + deviceDto.getLicenseKey()));
        
        if (!license.isActive()) {
            throw new RuntimeException("License is not active");
        }
        
        if (!license.canAddDevice()) {
            throw new RuntimeException("License has reached maximum device limit");
        }
        
        if (deviceRepository.existsByLicenseLicenseKeyAndDeviceName(deviceDto.getLicenseKey(), deviceDto.getDeviceName())) {
            throw new RuntimeException("Device with name " + deviceDto.getDeviceName() + " already exists for this license");
        }
        
        Device device = new Device();
        device.setLicense(license);
        device.setDeviceName(deviceDto.getDeviceName());
        device.setDeviceId(deviceDto.getDeviceId());
        device.setDeviceType(deviceDto.getDeviceType());
        device.setOperatingSystem(deviceDto.getOperatingSystem());
        device.setIpAddress(deviceDto.getIpAddress());
        device.setActive(deviceDto.isActive());
        device.setLastActivity(LocalDateTime.now());
        
        Device savedDevice = deviceRepository.save(device);
        return convertToDto(savedDevice);
    }
    
    public DeviceDto updateDevice(Long deviceId, DeviceDto deviceDto) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + deviceId));
        
        device.setDeviceName(deviceDto.getDeviceName());
        device.setDeviceId(deviceDto.getDeviceId());
        device.setDeviceType(deviceDto.getDeviceType());
        device.setOperatingSystem(deviceDto.getOperatingSystem());
        device.setIpAddress(deviceDto.getIpAddress());
        device.setActive(deviceDto.isActive());
        device.updateLastActivity();
        
        Device updatedDevice = deviceRepository.save(device);
        return convertToDto(updatedDevice);
    }
    
    public DeviceDto getDeviceById(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + deviceId));
        return convertToDto(device);
    }
    
    public List<DeviceDto> getDevicesByLicense(String licenseKey) {
        return deviceRepository.findByLicenseLicenseKey(licenseKey).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDto> getActiveDevicesByLicense(String licenseKey) {
        return deviceRepository.findActiveDevicesByLicenseKey(licenseKey).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDto> getActiveDevices() {
        return deviceRepository.findByActive(true).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDto> getDevicesByCustomer(String customerId) {
        return deviceRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<DeviceDto> searchDevices(String searchTerm) {
        return deviceRepository.findByDeviceNameOrDeviceIdContaining(searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public void removeDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + deviceId));
        deviceRepository.delete(device);
    }
    
    public void removeDeviceByLicenseAndName(String licenseKey, String deviceName) {
        Device device = deviceRepository.findByLicenseLicenseKeyAndDeviceName(licenseKey, deviceName)
                .orElseThrow(() -> new RuntimeException("Device not found with license: " + licenseKey + " and name: " + deviceName));
        deviceRepository.delete(device);
    }
    
    public void removeAllDevicesByLicense(String licenseKey) {
        List<Device> devices = deviceRepository.findByLicenseLicenseKey(licenseKey);
        deviceRepository.deleteAll(devices);
    }
    
    public void deactivateDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + deviceId));
        device.setActive(false);
        deviceRepository.save(device);
    }
    
    public void updateDeviceActivity(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found with ID: " + deviceId));
        device.updateLastActivity();
        deviceRepository.save(device);
    }
    
    public long getDeviceCountByLicense(String licenseKey) {
        return deviceRepository.countByLicenseKey(licenseKey);
    }
    
    public long getActiveDeviceCount() {
        return deviceRepository.countActiveDevices();
    }
    
    public boolean deviceExists(String licenseKey, String deviceName) {
        return deviceRepository.existsByLicenseLicenseKeyAndDeviceName(licenseKey, deviceName);
    }
    
    public List<DeviceDto> getInactiveDevices(LocalDateTime threshold) {
        return deviceRepository.findInactiveDevices(threshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private DeviceDto convertToDto(Device device) {
        DeviceDto dto = new DeviceDto();
        dto.setId(device.getId());
        dto.setLicenseKey(device.getLicense().getLicenseKey());
        dto.setDeviceName(device.getDeviceName());
        dto.setDeviceId(device.getDeviceId());
        dto.setDeviceType(device.getDeviceType());
        dto.setOperatingSystem(device.getOperatingSystem());
        dto.setIpAddress(device.getIpAddress());
        dto.setActive(device.isActive());
        dto.setLastActivity(device.getLastActivity());
        dto.setCreatedAt(device.getCreatedAt());
        dto.setUpdatedAt(device.getUpdatedAt());
        return dto;
    }
} 