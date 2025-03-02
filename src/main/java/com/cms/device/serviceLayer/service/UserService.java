package com.cms.device.serviceLayer.service;

import com.cms.device.entity.User;

import java.util.List;

public interface UserService {
    void save(User user);
    User findByPhone(String phone);
    List<User> findAll();
}
