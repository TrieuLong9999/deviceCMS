package com.cms.device.serviceLayer.service;

import com.cms.device.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FeedbackService {
    void save(Feedback feedback);
    Page<Feedback> findAll(Pageable pageable);
    List<Map<String, Object>> getRatingStats();
}
