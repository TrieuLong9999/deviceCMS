package com.cms.device.repository;

import com.cms.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    List<Device> findByLicenseLicenseKey(String licenseKey);
    
    Optional<Device> findByLicenseLicenseKeyAndDeviceName(String licenseKey, String deviceName);
    
    boolean existsByLicenseLicenseKeyAndDeviceName(String licenseKey, String deviceName);
    
    List<Device> findByActive(boolean active);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.license.licenseKey = :licenseKey")
    long countByLicenseKey(@Param("licenseKey") String licenseKey);
    
    @Query("SELECT d FROM Device d WHERE d.license.licenseKey = :licenseKey AND d.active = true")
    List<Device> findActiveDevicesByLicenseKey(@Param("licenseKey") String licenseKey);
    
    @Query("SELECT d FROM Device d WHERE d.lastActivity < :threshold")
    List<Device> findInactiveDevices(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.active = true")
    long countActiveDevices();
    
    @Query("SELECT d FROM Device d WHERE d.deviceName LIKE %:name% OR d.deviceId LIKE %:name%")
    List<Device> findByDeviceNameOrDeviceIdContaining(@Param("name") String name);
    
    @Query("SELECT d FROM Device d WHERE d.license.customer.customerId = :customerId")
    List<Device> findByCustomerId(@Param("customerId") String customerId);
} 