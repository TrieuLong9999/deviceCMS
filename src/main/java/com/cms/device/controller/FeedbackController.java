package com.cms.device.controller;

import com.cms.device.dto.request.FeedbackForm;
import com.cms.device.dto.response.ResponseListObject;
import com.cms.device.entity.Feedback;
import com.cms.device.serviceLayer.service.FeedbackService;
import com.cms.device.util.StaticCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("getData")
    public ResponseEntity<ResponseListObject> getFeedbackData(@RequestBody FeedbackForm feedbackForm){
        ResponseListObject responseListObject = new ResponseListObject();

        responseListObject.setCodeResponse(StaticCode.CodeResponse.Success);

        Pageable pageable = feedbackForm.toPageable();
        Page<Feedback> feedbacks = feedbackService.findAll(pageable);

        responseListObject.setData(feedbacks.get());
        responseListObject.setPage((long) feedbackForm.getPage());
        responseListObject.setPageSize((long) feedbackForm.getPageSize());
        responseListObject.setTotal(feedbacks.getTotalElements());
        return ResponseEntity.ok(responseListObject);
    }
}
