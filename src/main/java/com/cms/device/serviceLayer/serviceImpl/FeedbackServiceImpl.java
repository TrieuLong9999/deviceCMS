package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.repository.FeedbackRepository;
import com.cms.device.serviceLayer.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
}
