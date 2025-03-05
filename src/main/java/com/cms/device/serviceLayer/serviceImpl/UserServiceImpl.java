package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.User;
import com.cms.device.repository.UserRepository;
import com.cms.device.serviceLayer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Override
    public long count() {
        return  userRepository.count();
    }

    @Override
    public Long countUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth, List<String> userIds) {
        return userRepository.countUsersInCurrentMonth(startOfMonth,startOfNextMonth, userIds);
    }

    @Override
    public List<Map<String, Object>> getUserStatisticsByType() {
        return userRepository.countUsersByType();
    }

    @Override
    public List<User> findByListId(List<String> userIds) {
        return userRepository.findByListId(userIds);
    }


}
