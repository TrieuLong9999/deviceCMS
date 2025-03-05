package com.cms.device.serviceLayer.service;

import com.cms.device.entity.CustomerSubscriber;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CustomerSubscriberService {
    void save (CustomerSubscriber customerSubscriber);
    Long countUniqueUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth, List<String> userIds);
    List<Map<String, Object>> countUsersByService();
    List<Map<String, Object>> calculateMonthlyRevenue(Date startOfYear, Date endOfYear,int year);

    List<Map<String, Object>> getSubscriptionByDuration();

    List<Map<String, Object>> getSubscriptionByDuration(Date threeMonthsAgo);

}
