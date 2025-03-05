package com.cms.device.controller;

import com.cms.device.dto.BaseFormRequest;
import com.cms.device.dto.response.ResponseListObject;
import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.Feedback;
import com.cms.device.entity.User;
import com.cms.device.repository.CustomerExperienceRepository;
import com.cms.device.serviceLayer.service.ApiStatusLogService;
import com.cms.device.serviceLayer.service.CustomerExperienceService;
import com.cms.device.serviceLayer.service.FeedbackService;
import com.cms.device.serviceLayer.service.UserService;
import com.cms.device.util.StaticCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer-experience")
public class CustomerExperienceController {
    @Autowired
    private CustomerExperienceService customerExperienceService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ApiStatusLogService apiStatusLogService;
    @Autowired
    private UserService userService;
    @GetMapping("/platform-stats")
    public ResponseEntity<ResponseObject> getUserCountByPlatform() {
        ResponseObject responseObject = new ResponseObject();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(customerExperienceService.countUsersByPlatformWithYearlyMonthlyData());
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
    @PostMapping("feedback")
    public ResponseEntity<ResponseListObject> getListFeedBack(@RequestBody BaseFormRequest baseFormRequest){
        ResponseListObject responseListObject = new ResponseListObject();

        responseListObject.setCodeResponse(StaticCode.CodeResponse.Success);
        if(baseFormRequest.validate()) return ResponseEntity.ok(responseListObject);

        responseListObject.setPage((long)baseFormRequest.getPage());
        responseListObject.setPageSize((long)baseFormRequest.getPageSize());

        responseListObject.setData(feedbackService.findFeedbackWithUser(baseFormRequest.toPageable()));
        responseListObject.setTotal(feedbackService.countTotalFeedback());
        return ResponseEntity.ok(responseListObject);
    }
    @GetMapping("feedback-rating-platform")
    public ResponseEntity<ResponseObject> feedbackPlatform(){
        ResponseObject responseObject = new ResponseObject();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(feedbackService.getRatingStatistics());

        return ResponseEntity.ok(responseObject);

    }

}
