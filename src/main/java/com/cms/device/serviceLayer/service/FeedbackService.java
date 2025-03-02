package com.cms.device.serviceLayer.service;

import com.cms.device.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    void save(Feedback feedback);
    Page<Feedback> findAll(Pageable pageable);

}
