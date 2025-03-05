package com.cms.device.repository;

import com.cms.device.entity.User;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByPhone(String phone);
    @Aggregation(pipeline = {
            "{ '$match': { 'createDate': { '$gte': ?0, '$lt': ?1 }, '_id': { '$in': ?2 } } }",
            "{ '$count': 'totalUsers' }"
    })
    Long countUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth, List<String> userIds);

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$type', 'count': { '$sum': 1 } } }",
            "{ '$match': { '_id': { '$ne': null } } }",  // Lọc bỏ _id = null
            "{ '$sort': { '_id': 1 } }"
    })
    List<Map<String, Object>> countUsersByType();

    @Query("{ 'id': { '$in': ?0 } }")
    List<User> findByListId(List<String> ids);

}
