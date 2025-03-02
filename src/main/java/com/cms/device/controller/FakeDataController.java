package com.cms.device.controller;

import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.Feedback;
import com.cms.device.entity.User;
import com.cms.device.entity.Vendor;
import com.cms.device.serviceLayer.service.EntityStructureService;
import com.cms.device.serviceLayer.service.FeedbackService;
import com.cms.device.serviceLayer.service.UserService;
import com.cms.device.serviceLayer.service.VendorService;
import com.cms.device.util.StaticCode;
import com.cms.device.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("api/fake")
public class FakeDataController {
    @Autowired
    private VendorService vendorService;
    @Autowired
    private UserService userService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private EntityStructureService entityStructureService;

    @GetMapping("all")
    public ResponseEntity<ResponseObject> fakeALLData(){
        //TODO fake vendor
        ResponseObject responseObject = new ResponseObject();
        List<Vendor> vendorInit = new ArrayList<>();
        initVendor(vendorInit);

        for(Vendor vendor: vendorInit){
            if(vendorService.getByName(vendor.getShortName()) != null) continue;
            vendorService.save(vendor);
        }
        vendorInit.clear();
        List<User> userInit = new ArrayList<>();
        initUser(userInit);
        for (User user: userInit){
            if(userService.findByPhone(user.getPhone()) != null) continue;
            userService.save(user);
        }

        userInit.clear();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        return ResponseEntity.ok(responseObject);
    }
    @GetMapping("feedback")
    public ResponseEntity<ResponseObject> fakeFeedback(){
        ResponseObject responseObject = new ResponseObject();
        initFeedback();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        return ResponseEntity.ok(responseObject);
    }


    private void initVendor(List<Vendor> vendorInit){
        vendorInit = new ArrayList<>();
        Map<String, Object> mapVendor = new HashMap<>();

        mapVendor.put("Hikvision", "Hikvision");
        mapVendor.put("Axis", "Axis Communications");
        mapVendor.put("Dahua", "Dahua Communications");

        for (String key: mapVendor.keySet()){
            Vendor vendor = new Vendor();
            vendor.setId(StringUtil.generateUUID());
            vendor.setShortName(key);
            vendor.setName(String.valueOf(mapVendor.get(key)));
            vendor.setContact(key);
            vendor.setCountryCode("VI");
            vendorInit.add(vendor);
        }
    }

    private void initUser(List<User> userInit){
        // init user
        userInit = new ArrayList<>();
        Random random = new Random();

        for (int i = 0 ; i < 100; i++){
            User tempUser = new User();
            tempUser.setId(StringUtil.generateUUID());
            tempUser.setName("User"+i+1);
            tempUser.setPhone("090"+(1000000 + random.nextInt(9000000)));
            Date createDate = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            tempUser.setCreateDate(createDate);
            tempUser.setLastModified(new Date(createDate.getTime() + random.nextInt(1000000000)));
            userInit.add(tempUser);
        }
    }

    private void initFeedback(){
        List<User> allUser = userService.findAll();
        List<String> allUserId = allUser.stream().map(User::getId).toList();

        Random random = new Random();
        String[] platforms = {"web", "app", "tv"};
        String[] versions = {"1.0", "1.1", "2.0", "2.1", "3.0"};
        String[] messages = {
                "Great experience!", "Needs improvement.", "Love the app!",
                "It crashes sometimes.", "User-friendly interface.", "Great performance."
        };

        for (int i = 0 ; i < 1000; i++){
            Feedback tempFeedback = new Feedback();
            tempFeedback.setId(StringUtil.generateUUID());
            tempFeedback.setUserId(allUserId.get(random.nextInt(allUserId.size()-1)));
            tempFeedback.setRating(1 + 0.5f * random.nextInt(9));
            tempFeedback.setMessage( messages[random.nextInt(messages.length)]);
            tempFeedback.setPlatform(platforms[random.nextInt(platforms.length)]);
            tempFeedback.setVersion(versions[random.nextInt(versions.length)]);
            Date createAt = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            tempFeedback.setCreateAt(createAt);
            tempFeedback.setLastModified(new Date(createAt.getTime() + random.nextInt(1000000000)));
            feedbackService.save(tempFeedback);
        }

    }

    @GetMapping("structure")
    public ResponseEntity<ResponseObject> getStructureDatabase(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        Map<String,List<String>> allEntitiesStructure = entityStructureService.getAllEntitiesStructure();

        responseObject.setData(allEntitiesStructure);

        return ResponseEntity.ok(responseObject);
    }

}
