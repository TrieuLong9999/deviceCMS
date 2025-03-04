package com.cms.device.serviceLayer.service;

import com.cms.device.entity.Device;

import java.util.List;
import java.util.Map;

public interface DeviceService {
    void save (Device device);
    List<Map<String, Object>> getDeviceStatsByVendor();

}
