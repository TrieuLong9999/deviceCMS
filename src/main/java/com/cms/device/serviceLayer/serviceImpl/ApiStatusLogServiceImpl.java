package com.cms.device.serviceLayer.serviceImpl;

import com.cms.device.entity.ApiStatusLog;
import com.cms.device.repository.ApiStatusLogRepository;
import com.cms.device.serviceLayer.service.ApiStatusLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApiStatusLogServiceImpl implements ApiStatusLogService {
    @Autowired
    private ApiStatusLogRepository apiStatusLogRepository;
    @Override
    public void save(ApiStatusLog apiStatusLog) {
        if(apiStatusLog != null) apiStatusLogRepository.save(apiStatusLog);
    }

    @Override
    public List<Map<String, Object>> getStatusCodeDistribution() {
        return apiStatusLogRepository.getStatusCodeDistribution();
    }

    @Override
    public List<Map<String, Object>> getResponseTimeDistribution() {
        return apiStatusLogRepository.getResponseTimeDistribution();
    }
}
