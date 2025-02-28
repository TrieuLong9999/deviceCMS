package com.cms.device.repository;

import com.cms.device.entity.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends MongoRepository<Vendor, String> {
    Vendor findByName(String name);
}
