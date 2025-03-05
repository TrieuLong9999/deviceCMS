package com.cms.device.serviceLayer.service;


import com.cms.device.entity.CustomerExperience;

import java.util.List;
import java.util.Map;

public interface CustomerExperienceService {
    CustomerExperience getById(String id);
    void save(CustomerExperience customerExperience);
    CustomerExperience findByUserIdAndPlatform(String userId, String platform);
    List<Map<String, Object>>  countUsersByPlatform();
    List<Map<String, Object>> countUsersByPlatformWithYearlyMonthlyData();

}
