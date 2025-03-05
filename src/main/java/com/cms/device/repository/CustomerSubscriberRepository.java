package com.cms.device.repository;

import com.cms.device.entity.CustomerSubscriber;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface CustomerSubscriberRepository extends MongoRepository<CustomerSubscriber, String> {
    @Aggregation(pipeline = {
            "{ '$match': { 'createDate': { '$gte': ?0, '$lt': ?1 }, 'userId': { '$in': ?2 } } }",
            "{ '$group': { '_id': '$userId' } }",
            "{ '$count': 'totalUsers' }"
    })
    Long countUniqueUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth, List<String> userIds);

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$service', 'totalUsing': { '$sum': 1 } } }",
            "{ '$sort': { 'totalUsing': -1 } }"
    })
    List<Map<String, Object>> countUsersByService();
    @Aggregation(pipeline = {
            "{ '$match': { "
                    + "'createDate': { '$gte': ?0, '$lt': ?1 }, "
                    + "'$expr': { '$eq': [{ '$year': '$createDate' }, ?2] }"
                    + "} }",
            "{ '$group': { "
                    + "'_id': { 'year': { '$year': '$createDate' }, 'month': { '$month': '$createDate' } }, "
                    + "'totalRevenue': { '$sum': '$price' }"
                    + "} }",
            "{ '$sort': { '_id.year': 1, '_id.month': 1 } }"
    })
    List<Map<String, Object>> calculateMonthlyRevenue(Date startOfYear, Date endOfYear, int year);
    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$duration', 'count': { '$sum': 1 } } }",
            "{ '$sort': { '_id': 1 } }"
    })
    List<Map<String, Object>> getSubscriptionByDuration();


    @Aggregation(pipeline = {
            "{ '$match': { 'createDate': { '$gte': ?0 } } }",
            "{ '$group': { '_id': { 'year': { '$year': '$createDate' }, 'month': { '$month': '$createDate' }, 'duration': '$duration' }, 'count': { '$sum': 1 } } }",
            "{ '$group': { '_id': { 'year': '$_id.year', 'month': '$_id.month' }, 'data': { '$push': { '_id': '$_id.duration', 'count': '$count' } } } }",
            "{ '$project': { 'timeData': { '$concat': [ { '$toString': '$_id.year' }, '-', { '$toString': '$_id.month' } ] }, 'data': 1, '_id': 0 } }",
            "{ '$sort': { 'timeData': -1 } }"
    })
    List<Map<String, Object>> getSubscriptionByDuration(Date threeMonthsAgo);



}
