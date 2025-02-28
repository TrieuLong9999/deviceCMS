package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.repository.UserRepository;
import com.cms.device.serviceLayer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
}
