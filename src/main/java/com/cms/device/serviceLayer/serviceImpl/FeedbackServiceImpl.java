package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.Feedback;
import com.cms.device.repository.FeedbackRepository;
import com.cms.device.serviceLayer.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public Page<Feedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Override
    public List<Map<String, Object>> getRatingStats() {
        return feedbackRepository.getAverageRatingByPlatform();
    }
}
