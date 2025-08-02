package com.cms.device.repository;

import com.cms.device.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    
    Optional<License> findByLicenseKey(String licenseKey);
    
    boolean existsByLicenseKey(String licenseKey);
    
    List<License> findByCustomerCustomerId(String customerId);
    
    List<License> findByActive(boolean active);
    
    List<License> findByLicenseType(String licenseType);
    
    @Query("SELECT l FROM License l WHERE l.expiryDate IS NOT NULL AND l.active = true")
    List<License> findActiveLicensesWithExpiryDate();
    
    @Query("SELECT COUNT(l) FROM License l WHERE l.active = true")
    long countActiveLicenses();
    
    @Query("SELECT COUNT(l) FROM License l WHERE l.active = false")
    long countInactiveLicenses();
    
    @Query("SELECT l FROM License l WHERE l.customer.customerId = :customerId AND l.active = true")
    List<License> findActiveLicensesByCustomerId(@Param("customerId") String customerId);
    
    @Modifying
    @Query("UPDATE License l SET l.active = false, l.licenseType = 'Hết hạn' " +
           "WHERE l.expiryDate IS NOT NULL AND l.active = true " +
           "AND l.licenseType NOT IN ('Chưa kích hoạt', 'Hết hạn') " +
           "AND TO_TIMESTAMP(l.expiryDate, 'DD/MM/YYYY HH24:MI:SS') < NOW()")
    int updateExpiredLicenses();
} 