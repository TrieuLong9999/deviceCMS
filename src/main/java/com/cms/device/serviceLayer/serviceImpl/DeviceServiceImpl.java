package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.Device;
import com.cms.device.repository.DeviceRepository;
import com.cms.device.serviceLayer.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public void save(Device device) {
        if(device != null)
        deviceRepository.save(device);
    }

    @Override
    public List<Map<String, Object>> getDeviceStatsByVendor() {
        return deviceRepository.getDeviceStatsByVendor();
    }

    @Override
    public List<Map<String, Object>> getDeviceTypeDistributionByVendors(List<String> vendorIds) {
        return deviceRepository.getDeviceTypeDistributionByVendors(vendorIds);
    }

    @Override
    public List<Map<String, Object>> getNewDevicesDays(Date fromDate) {
        return deviceRepository.getNewDevicesDays(fromDate);
    }

    @Override
    public List<Map<String, Object>> getVendorDeviceStats(Pageable pageable) {
        return deviceRepository.getVendorDeviceStats(pageable);
    }

    @Override
    public Long getTotalVendorDeviceStats() {
        return deviceRepository.getTotalVendorDeviceStats();
    }

    @Override
    public Device findBySerialNumberAndVendorId(String serialNumber, String vendorId) {
        return null;
    }
}
