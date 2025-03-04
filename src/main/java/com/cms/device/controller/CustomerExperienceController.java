package com.cms.device.controller;

import com.cms.device.dto.response.ResponseObject;
import com.cms.device.repository.CustomerExperienceRepository;
import com.cms.device.serviceLayer.service.ApiStatusLogService;
import com.cms.device.serviceLayer.service.CustomerExperienceService;
import com.cms.device.serviceLayer.service.FeedbackService;
import com.cms.device.util.StaticCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer-experience")
public class CustomerExperienceController {
    @Autowired
    private CustomerExperienceService customerExperienceService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ApiStatusLogService apiStatusLogService;
    @GetMapping("/platform-stats")
    public ResponseEntity<ResponseObject> getUserCountByPlatform() {
        ResponseObject responseObject = new ResponseObject();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(customerExperienceService.countUsersByPlatform());
        return ResponseEntity.ok(responseObject);
    }
    @GetMapping("feedback-rating")
    public ResponseEntity<ResponseObject> getRatingStats(){
        ResponseObject responseObject = new ResponseObject();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(feedbackService.getRatingStats());
        return ResponseEntity.ok(responseObject);

    }
    @GetMapping("/status-code-distribution")
    public ResponseEntity<ResponseObject> getStatusCodeDistribution() {
        ResponseObject responseObject = new ResponseObject();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(apiStatusLogService.getStatusCodeDistribution());
        return ResponseEntity.ok(responseObject);
    }
    @GetMapping("/response-time-distribution")
    public ResponseEntity<ResponseObject> getResponseTimeDistribution() {
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(apiStatusLogService.getResponseTimeDistribution());
        return ResponseEntity.ok(responseObject);
    }

}
