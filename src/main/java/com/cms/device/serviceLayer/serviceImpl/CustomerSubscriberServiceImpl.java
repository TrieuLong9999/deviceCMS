package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.CustomerSubscriber;
import com.cms.device.repository.CustomerSubscriberRepository;
import com.cms.device.serviceLayer.service.CustomerSubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CustomerSubscriberServiceImpl implements CustomerSubscriberService {
    @Autowired
    private CustomerSubscriberRepository customerSubscriberRepository;

    @Override
    public void save(CustomerSubscriber customerSubscriber) {
        if(customerSubscriber != null) customerSubscriberRepository.save(customerSubscriber);
    }

    @Override
    public Long countUniqueUsersInCurrentMonth(Date startOfMonth, Date startOfNextMonth, List<String> userIds) {
        return customerSubscriberRepository.countUniqueUsersInCurrentMonth(startOfMonth,startOfNextMonth, userIds);
    }

    @Override
    public List<Map<String, Object>> countUsersByService() {
        return customerSubscriberRepository.countUsersByService();
    }

    @Override
    public List<Map<String, Object>> calculateMonthlyRevenue(Date startOfYear, Date endOfYear,int year) {
        return customerSubscriberRepository.calculateMonthlyRevenue(startOfYear, endOfYear, year);
    }

    @Override
    public List<Map<String, Object>> getSubscriptionByDuration() {
        return customerSubscriberRepository.getSubscriptionByDuration();
    }

    @Override
    public List<Map<String, Object>> getSubscriptionByDuration(Date threeMonthsAgo) {
        return customerSubscriberRepository.getSubscriptionByDuration(threeMonthsAgo);
    }
}
