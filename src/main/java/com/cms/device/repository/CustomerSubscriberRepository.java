package com.cms.device.repository;

import com.cms.device.entity.CustomerSubscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSubscriberRepository extends MongoRepository<CustomerSubscriber, String> {
}
