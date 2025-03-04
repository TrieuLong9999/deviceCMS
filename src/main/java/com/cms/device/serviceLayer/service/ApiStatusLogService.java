package com.cms.device.serviceLayer.service;

import com.cms.device.entity.ApiStatusLog;

import java.util.List;
import java.util.Map;

public interface ApiStatusLogService {
    void save(ApiStatusLog apiStatusLog);
    List<Map<String, Object>> getStatusCodeDistribution();
    List<Map<String, Object>> getResponseTimeDistribution();
}
