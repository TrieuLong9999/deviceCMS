package com.cms.device.serviceLayer.service;

import com.cms.device.entity.Vendor;

import java.util.List;

public interface VendorService {
    List<Vendor> getAll();
    void  save(Vendor vendor);

    Vendor getByName(String name);
}
