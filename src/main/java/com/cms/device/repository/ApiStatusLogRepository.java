package com.cms.device.repository;

import com.cms.device.entity.ApiStatusLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ApiStatusLogRepository extends MongoRepository<ApiStatusLog, String> {
    @Aggregation("{ '$group': { '_id': '$statusCode', 'count': { '$sum': 1 } } }")
    List<Map<String, Object>> getStatusCodeDistribution();

    @Aggregation({
            "{ '$group': {"
                    + "  '_id': {"
                    + "    '$switch': {"
                    + "      'branches': ["
                    + "        { 'case': { '$lt': ['$durationTime', 1000] }, 'then': '0-1s' },"
                    + "        { 'case': { '$lt': ['$durationTime', 2000] }, 'then': '1-2s' },"
                    + "        { 'case': { '$lt': ['$durationTime', 4000] }, 'then': '2-4s' }"
                    + "      ],"
                    + "      'default': '>4s'"
                    + "    }"
                    + "  },"
                    + "  'count': { '$sum': 1 }"
                    + "}}"
    })
    List<Map<String, Object>> getResponseTimeDistribution();

}
