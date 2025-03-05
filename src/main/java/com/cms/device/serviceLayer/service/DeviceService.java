package com.cms.device.serviceLayer.service;

import com.cms.device.entity.Device;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DeviceService {
    void save (Device device);
    List<Map<String, Object>> getDeviceStatsByVendor();
    List<Map<String, Object>> getDeviceTypeDistributionByVendors(List<String> vendorIds);
    List<Map<String, Object>> getNewDevicesDays(Date fromDate);
    List<Map<String, Object>> getVendorDeviceStats(Pageable pageable);
    Long getTotalVendorDeviceStats();
    Device findBySerialNumberAndVendorId(String serialNumber, String vendorId);
}
