package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.User;
import com.cms.device.repository.UserRepository;
import com.cms.device.serviceLayer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(User user) {
        if(user != null) userRepository.save(user);
    }

    @Override
    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
