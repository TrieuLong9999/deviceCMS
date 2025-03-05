package com.cms.device.repository;

import com.cms.device.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Page<Feedback> findAll(Pageable pageable);
    @Aggregation("{ '$group': { '_id': '$platform', 'averageRating': { '$avg': '$rating' }, 'count': { '$sum': 1 } } }")
    List<Map<String, Object>> getAverageRatingByPlatform();
//    @Aggregation(pipeline = {
//            "{ '$lookup': { 'from': 'User', 'localField': 'userId', 'foreignField': '_id', 'as': 'user' } }",
//            "{ '$match': { 'user': { '$ne': [] } } }", // Chỉ lấy feedback có userId tồn tại
//            "{ '$sort': { 'createAt': -1 } }"
//    })
    Page<Feedback> findAllByOrderByCreateAtDesc(Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'User', 'localField': 'userId', 'foreignField': '_id', 'as': 'user' } }",
            "{ '$unwind': { 'path': '$user', 'preserveNullAndEmptyArrays': true } }",
            "{ '$project': { "
                    + "'id': 1, "
                    + "'userId': 1, "
                    + "'name': '$user.name', " // Lấy name từ User
                    + "'rating': 1, "
                    + "'message': 1, "
                    + "'platform': 1, "
                    + "'version': 1, "
                    + "'createAt': 1 "
                    + "} }",
            "{ '$sort': { 'createAt': -1 } }"
    })
    List<Map<String, Object>> findFeedbackWithUser(Pageable pageable);

    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'User', 'localField': 'userId', 'foreignField': '_id', 'as': 'user' } }",
            "{ '$unwind': { 'path': '$user', 'preserveNullAndEmptyArrays': true } }",
            "{ '$count': 'totalElements' }"
    })
    Long countTotalFeedback();

    @Aggregation(pipeline = {
            "{ '$group': { '_id': { 'rating': '$rating', 'platform': '$platform' }, 'count': { '$sum': 1 } } }",
            "{ '$group': { '_id': '$_id.rating', 'platforms': { '$push': { 'platform': '$_id.platform', 'count': '$count' } } } }",
            "{ '$sort': { '_id': -1 } }"
    })
    List<Map<String, Object>> getRatingStatistics();


}
