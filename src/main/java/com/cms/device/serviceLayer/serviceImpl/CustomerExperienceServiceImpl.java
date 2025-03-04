package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.CustomerExperience;
import com.cms.device.repository.CustomerExperienceRepository;
import com.cms.device.serviceLayer.service.CustomerExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    @Override
    public void save(CustomerExperience customerExperience) {
        if (customerExperience != null) customerExperienceRepository.save(customerExperience);
    }

    @Override
    public CustomerExperience findByUserIdAndPlatform(String userId, String platform) {
        return customerExperienceRepository.findByUserIdAndPlatform(userId,platform);
    }

    @Override
    public List<Map<String, Object>> countUsersByPlatform() {
        return customerExperienceRepository.countUsersByPlatform();
    }
}
