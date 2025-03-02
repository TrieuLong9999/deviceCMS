package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.Device;
import com.cms.device.repository.DeviceRepository;
import com.cms.device.serviceLayer.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public void save(Device device) {
        if(device != null)
        deviceRepository.save(device);
    }
}
