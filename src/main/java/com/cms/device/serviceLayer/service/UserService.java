package com.cms.device.serviceLayer.service;

import com.cms.device.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService {
    void save(User user);
    User findByPhone(String phone);
    List<User> findAll();
    long count();
    Long countUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth,List<String> userIds);
    List<Map<String, Object>> getUserStatisticsByType();

    List<User> findByListId(List<String> userIds);


}
