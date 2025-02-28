package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.CustomerExperience;
import com.cms.device.repository.CustomerExperienceRepository;
import com.cms.device.serviceLayer.service.CustomerExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerExperienceServiceImpl implements CustomerExperienceService {
    @Autowired
    private CustomerExperienceRepository customerExperienceRepository;

    @Override
    public CustomerExperience getById(String id) {
        Optional<CustomerExperience> data = customerExperienceRepository.findById(id);
        return data.get();
    }
}
