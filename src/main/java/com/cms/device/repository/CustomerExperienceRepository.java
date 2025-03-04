package com.cms.device.repository;

import com.cms.device.entity.CustomerExperience;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerExperienceRepository extends MongoRepository<CustomerExperience, String> {
    CustomerExperience findByUserIdAndPlatform(String userId, String platform);
    @Aggregation("{ '$group': { '_id': '$platform', 'count': { '$sum': 1 } } }")
    List<Map<String, Object>> countUsersByPlatform();
}
