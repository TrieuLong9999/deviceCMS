package com.cms.device.controller;

import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.User;
import com.cms.device.serviceLayer.service.CustomerExperienceService;
import com.cms.device.serviceLayer.service.CustomerSubscriberService;
import com.cms.device.serviceLayer.service.UserService;
import com.cms.device.util.StaticCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("api/business")
public class BusinessActivitiesController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerSubscriberService customerSubscriberService;
    @GetMapping("user-statistic")
    public ResponseEntity<ResponseObject> userStatistic(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        // Lấy ngày đầu và ngày đầu tháng kế tiếp
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);
        // Convert sang Date
        Date startOfMonth = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date startOfNextMonth = Date.from(firstDayOfNextMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<User> allUser = userService.findAll();
        List<String> allUserIds = allUser.stream().map(User::getId).toList();
        // Gọi repository
        Long totalUserNewInMonth = userService.countUsersInCurrentMonth(startOfMonth, startOfNextMonth, allUserIds);
        // Long total using in month
        Long totalUsingThisMonth = customerSubscriberService.countUniqueUsersInCurrentMonth(startOfMonth,startOfNextMonth, allUserIds);


        Map<String, Object> dataResponse = new HashMap<>();

        dataResponse.put("totalUser", allUser.size());
        dataResponse.put("newUserInMonth", totalUserNewInMonth);
        dataResponse.put("userNotUsing", allUser.size() - totalUsingThisMonth);

        responseObject.setData(dataResponse);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("packages-and-service")
    public ResponseEntity<ResponseObject> packetAndService(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(customerSubscriberService.countUsersByService());

        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("revenue-monthly")
    public ResponseEntity<ResponseObject> revenueMonth(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        // Lấy ngày đầu năm
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        Date startOfYear = calendar.getTime();

        // Lấy ngày đầu của năm sau
        calendar.add(Calendar.YEAR, 1);
        Date endOfYear = calendar.getTime();

        responseObject.setData(customerSubscriberService.calculateMonthlyRevenue(startOfYear,endOfYear, currentYear));

        return ResponseEntity.ok(responseObject);
    }
    @GetMapping("type-user")
    public ResponseEntity<ResponseObject> userByType(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        responseObject.setData(userService.getUserStatisticsByType());
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("packets-and-duration")
    public ResponseEntity<ResponseObject> packetsAndDuration(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        responseObject.setData(customerSubscriberService.getSubscriptionByDuration());
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("duration-total-permonth")
    public ResponseEntity<ResponseObject> durationTotalPerMonth(@RequestParam(value = "month",defaultValue = "3") int month){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -month);
        Date threeMonthsAgo = calendar.getTime();

        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(customerSubscriberService.getSubscriptionByDuration(threeMonthsAgo));
        return ResponseEntity.ok(responseObject);
    }

}
