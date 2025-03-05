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
    @Aggregation(pipeline = {
            "{ '$project': { "
                    + "'platform': 1, "
                    + "'year': { '$year': '$createDate' }, "
                    + "'month': { '$month': '$createDate' } "
                    + "} }",
            "{ '$group': { "
                    + "'_id': { 'platform': '$platform', 'year': '$year', 'month': '$month' }, "
                    + "'count': { '$sum': 1 } "
                    + "} }",
            "{ '$group': { "
                    + "'_id': '$_id.platform', "
                    + "'total': { '$sum': '$count' }, "
                    + "'monthlyData': { '$push': { 'year': '$_id.year', 'month': '$_id.month', 'count': '$count' } } "
                    + "} }",
            "{ '$sort': { '_id': 1 } }"
    })
    List<Map<String, Object>> countUsersByPlatformWithYearlyMonthlyData();


}
