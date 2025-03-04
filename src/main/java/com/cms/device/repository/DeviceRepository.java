package com.cms.device.repository;

import com.cms.device.entity.Device;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$vendorId', deviceCount: { $sum: 1 } } }", // Nhóm theo vendorId và đếm thiết bị
            })
    List<Map<String, Object>> getDeviceStatsByVendor();




}
