package com.cms.device.serviceLayer.service;


import com.cms.device.entity.CustomerExperience;

public interface CustomerExperienceService {
    CustomerExperience getById(String id);
    void save(CustomerExperience customerExperience);
}
