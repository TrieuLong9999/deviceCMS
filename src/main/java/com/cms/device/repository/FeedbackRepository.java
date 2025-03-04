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
}
