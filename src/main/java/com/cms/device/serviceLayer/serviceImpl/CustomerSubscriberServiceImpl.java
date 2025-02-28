package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.repository.CustomerSubscriberRepository;
import com.cms.device.serviceLayer.service.CustomerSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerSubscriberServiceImpl implements CustomerSubscriberService {
    @Autowired
    private CustomerSubscriberRepository customerSubscriberRepository;
}
