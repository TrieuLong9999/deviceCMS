package com.cms.device.repository;

import com.cms.device.entity.CustomerExperience;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerExperienceRepository extends MongoRepository<CustomerExperience, String> {
}
