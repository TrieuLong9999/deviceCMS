package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.Vendor;
import com.cms.device.repository.VendorRepository;
import com.cms.device.serviceLayer.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {
    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public List<Vendor> getAll() {
        return vendorRepository.findAll();
    }

    @Override
    public void save(Vendor vendor) {
        if(vendor == null) return;
        vendorRepository.save(vendor);
    }

    @Override
    public Vendor getByName(String name) {
        return vendorRepository.findByName(name);
    }
}
