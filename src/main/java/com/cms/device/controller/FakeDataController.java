package com.cms.device.controller;

import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.*;
import com.cms.device.serviceLayer.service.*;
import com.cms.device.util.StaticCode;
import com.cms.device.util.StringUtil;
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

    @Autowired
    private CustomerExperienceService customerExperienceService;
    @Autowired
    private ApiStatusLogService apiStatusLogService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CustomerSubscriberService customerSubscriberService;
    @GetMapping("all")
    public ResponseEntity<ResponseObject> fakeALLData(){
        //TODO fake vendor
        ResponseObject responseObject = new ResponseObject();
        List<Vendor> vendorInit = new ArrayList<>();
        vendorInit  =  initVendor();

        for(Vendor vendor: vendorInit){
            if(vendorService.findByShortName(vendor.getShortName()) != null) continue;
            vendorService.save(vendor);
        }
        vendorInit.clear();
        List<User> userInit = initUser();
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


    @GetMapping("customerExperience")
    public ResponseEntity<ResponseObject> fakeCustomerExp(){
        ResponseObject responseObject = new ResponseObject();
        initCustomerExp();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        return ResponseEntity.ok(responseObject);
    }
    private void initCustomerExp(){
        List<User> allUser = userService.findAll();
        List<String> allUserId = allUser.stream().map(User::getId).toList();

        if(allUserId.isEmpty()) return;
        Random random = new Random();
        String[] platforms = {"web", "app", "tv"};
        String[] versions = {"1.0", "1.1", "2.0", "2.1", "3.0"};
        String[] messages = {
                "Great experience!", "Needs improvement.", "Love the app!",
                "It crashes sometimes.", "User-friendly interface.", "Great performance."
        };

        for (int i = 0 ; i < 1000; i++){
            CustomerExperience customerExperience = new CustomerExperience();
            customerExperience.setId(StringUtil.generateUUID());
            customerExperience.setUserId(allUserId.get(random.nextInt(allUserId.size()-1)));
//            customerExperience.setMessage( messages[random.nextInt(messages.length)]);
            customerExperience.setPlatform(platforms[random.nextInt(platforms.length)]);
            customerExperience.setVersion(versions[random.nextInt(versions.length)]);
            Date createAt = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            customerExperience.setCreateDate(createAt);
            customerExperience.setLastModified(new Date(createAt.getTime() + random.nextInt(1000000000)));

            if(customerExperienceService.findByUserIdAndPlatform(customerExperience.getUserId(), customerExperience.getPlatform()) != null) continue;

            customerExperienceService.save(customerExperience);
        }

    }
    private List<Vendor> initVendor(){
        List<Vendor> vendorInit = new ArrayList<>();
        Map<String, Object> mapVendor = new HashMap<>();

        mapVendor.put("Hikvision", "Hikvision");
        mapVendor.put("Axis", "Axis Communications");
        mapVendor.put("Dahua", "Dahua Communications");
        mapVendor.put("Rang dong", "Rang Dong Communications");
        mapVendor.put("Imou", "Imou Communications");
        mapVendor.put("Xiaomi", "Xiaomi Communications");

        for (String key: mapVendor.keySet()){
            Vendor vendor = new Vendor();
            vendor.setId(StringUtil.generateUUID());
            vendor.setShortName(key);
            vendor.setName(String.valueOf(mapVendor.get(key)));
            vendor.setContact(key);
            vendor.setCountryCode("VI");
            vendorInit.add(vendor);
        }
        return vendorInit;
    }

    private List<User> initUser(){
        // init user
        List<User> userInit = new ArrayList<>();
        Random random = new Random();
        // Lấy tất cả các loại thiết bị
        User.UserType[] userTypes = User.UserType.values();

        for (int i = 0 ; i < 10000; i++){
            User tempUser = new User();
            tempUser.setId(StringUtil.generateUUID());
            tempUser.setName("User"+i+1);
            tempUser.setPhone("090"+(1000000 + random.nextInt(9000000)));
            Date createDate = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            tempUser.setCreateDate(createDate);
            tempUser.setLastModified(new Date(createDate.getTime() + random.nextInt(1000000000)));
            tempUser.setType(userTypes[random.nextInt(userTypes.length)].ordinal() + 1);
            userInit.add(tempUser);
        }
        return userInit;
    }

    private void initFeedback(){
        List<User> allUser = userService.findAll();
        List<String> allUserId = allUser.stream().map(User::getId).toList();

        if(allUserId.isEmpty()) return;
        Random random = new Random();
        String[] platforms = {"web", "app", "tv"};
        String[] versions = {"1.0", "1.1", "2.0", "2.1", "3.0"};
        String[] messages = {
                "Great experience!", "Needs improvement.", "Love the app!",
                "It crashes sometimes.", "User-friendly interface.", "Great performance."
        };

        for (int i = 0 ; i < 10000; i++){
            Feedback tempFeedback = new Feedback();
            User userChoose = allUser.get(random.nextInt(allUserId.size()-1));

            tempFeedback.setId(StringUtil.generateUUID());
            tempFeedback.setUserId(userChoose.getId());
            tempFeedback.setName(userChoose.getName());

            tempFeedback.setRating(1f + random.nextInt(5)); // Random từ 1 đến 5
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

    @GetMapping("api-status-log")
    public ResponseEntity<ResponseObject> apiStatusLog(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        Random random = new Random();
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        String[] urls = {"/api/users", "/api/orders", "/api/products", "/api/payments"};
        Integer[] statusCodes = {200, 401, 100, 500};

        for (int i = 0; i < 10000; i++){
            ApiStatusLog log = new ApiStatusLog();
            log.setId(StringUtil.generateUUID());
            log.setUrl(urls[random.nextInt(urls.length)]);
            log.setMethod(methods[random.nextInt(methods.length)]);
            log.setStatusCode(statusCodes[random.nextInt(statusCodes.length)]);
            log.setDurationTime((long) random.nextInt(5_001));
            apiStatusLogService.save(log);
        }
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("device")
    public ResponseEntity<ResponseObject> fakeDevice(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        Random random = new Random();
        List<Vendor> allVendor = vendorService.getAll();

        List<String> allVendorId = allVendor.stream().map(Vendor::getId).toList();
        // Lấy tất cả các loại thiết bị
        Device.DeviceType[] deviceTypes = Device.DeviceType.values();



        for(int i = 0; i< 1000; i++){
            Device device = new Device();
            device.setId(StringUtil.generateUUID());
            // Tạo tên và model ngẫu nhiên
            device.setName("Device " + (i + 1));
            device.setModel("Model " + (i + 1));
            // Tạo serialNumber ngẫu nhiên
            device.setSerialNumber("SN-" + (random.nextInt(1000000) + 100000));
            // Chọn ngẫu nhiên vendorId từ danh sách vendor
            device.setVendorId(allVendorId.get(random.nextInt(allVendorId.size() - 1)));
            // Chọn ngẫu nhiên loại thiết bị từ DeviceType
            device.setType(deviceTypes[random.nextInt(deviceTypes.length)].ordinal() + 1);
            // Cập nhật ngày tạo và ngày sửa đổi ngẫu nhiên
            Date createAt = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            device.setCreateDate(createAt);
            device.setLastModified(new Date(createAt.getTime() + random.nextInt(1000000000)));
            deviceService.save(device);
        }
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("service")
    public ResponseEntity<ResponseObject> fakeService(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        Random random = new Random();


        List<User> allUser = userService.findAll();

        List<String> allUserId = allUser.stream().map(User::getId).toList();
        List<String> SERVICES = Arrays.asList("record", "cloud");
        Map<String, Long> PRICE_MAP = Map.of(
                "record", 50000L,
                "cloud", 100000L
        );
        List<Long> DURATION_OPTIONS = Arrays.asList(1L, 3L, 7L);
        for (Integer i = 0 ; i < 1000; i++){
            CustomerSubscriber customerSubscriber = new CustomerSubscriber();
            customerSubscriber.setId(StringUtil.generateUUID());
            customerSubscriber.setUserId(allUserId.get(random.nextInt(allUserId.size() - 1)));

            // Chọn service ngẫu nhiên
            String selectedService = SERVICES.get(random.nextInt(SERVICES.size()));
            customerSubscriber.setService(selectedService);

            // Chọn thời gian sử dụng từ danh sách (1, 3, 7 ngày)
            customerSubscriber.setDuration(DURATION_OPTIONS.get(random.nextInt(DURATION_OPTIONS.size())));

            customerSubscriber.setPrice(customerSubscriber.getDuration()*(PRICE_MAP.get(selectedService)));

            // Gán ngày tạo ngẫu nhiên trong vòng 3 tháng gần đây
            long millisInThreeMonths = 90L * 24 * 60 * 60 * 1000;
            Date createDate = new Date(System.currentTimeMillis() - random.nextLong(millisInThreeMonths));
            customerSubscriber.setCreateDate(createDate);
            customerSubscriber.setLastModified(new Date());

            customerSubscriberService.save(customerSubscriber);
        }
        return ResponseEntity.ok(responseObject);

    }
    @GetMapping("all-in-one")
    public ResponseEntity<ResponseObject> allInOne(){
        ResponseObject responseObject = new ResponseObject();
        List<Vendor> vendorInit = new ArrayList<>();
        vendorInit  =  initVendor();

        for(Vendor vendor: vendorInit){
            if(vendorService.findByShortName(vendor.getShortName()) != null) continue;
            vendorService.save(vendor);
        }
        vendorInit.clear();
        List<User> userInit = initUser();
        for (User user: userInit){
            if(userService.findByPhone(user.getPhone()) != null) continue;
            userService.save(user);
        }
        userInit.clear();
        // init feedbackUser

        initFeedback();

        //TODO init api status log
        initApiStatusLog();

        //TODO init device

        initDevice();

        //TODO
        initService();

        initCustomerExp();

        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        return ResponseEntity.ok(responseObject);



    }
    private void initService(){
        Random random = new Random();

        List<User> allUser = userService.findAll();

        List<String> allUserId = allUser.stream().map(User::getId).toList();
        List<String> SERVICES = Arrays.asList("record", "cloud");
        Map<String, Long> PRICE_MAP = Map.of(
                "record", 50000L,
                "cloud", 100000L
        );
        List<Long> DURATION_OPTIONS = Arrays.asList(1L, 3L, 7L);
        for (Integer i = 0 ; i < 1000; i++){
            CustomerSubscriber customerSubscriber = new CustomerSubscriber();
            customerSubscriber.setId(StringUtil.generateUUID());
            customerSubscriber.setUserId(allUserId.get(random.nextInt(allUserId.size() - 1)));

            // Chọn service ngẫu nhiên
            String selectedService = SERVICES.get(random.nextInt(SERVICES.size()));
            customerSubscriber.setService(selectedService);

            // Chọn thời gian sử dụng từ danh sách (1, 3, 7 ngày)
            customerSubscriber.setDuration(DURATION_OPTIONS.get(random.nextInt(DURATION_OPTIONS.size())));
            customerSubscriber.setPrice(customerSubscriber.getDuration()*(PRICE_MAP.get(selectedService)));

            // Gán ngày tạo ngẫu nhiên trong vòng 3 tháng gần đây
            long millisInThreeMonths = 90L * 24 * 60 * 60 * 1000;
            Date createDate = new Date(System.currentTimeMillis() - random.nextLong(millisInThreeMonths));
            customerSubscriber.setCreateDate(createDate);
            customerSubscriber.setLastModified(new Date());

            customerSubscriberService.save(customerSubscriber);
        }
    }
    private void initDevice(){
        Random random = new Random();
        List<Vendor> allVendor = vendorService.getAll();
        List<String> allVendorId = allVendor.stream().map(Vendor::getId).toList();
        // Lấy tất cả các loại thiết bị
        Device.DeviceType[] deviceTypes = Device.DeviceType.values();

        for(int i = 0; i< 10000; i++){
            Device device = new Device();
            device.setId(StringUtil.generateUUID());
            // Tạo tên và model ngẫu nhiên
            device.setName("Device " + (i + 1));
            device.setModel("Model " + (i + 1));
            // Tạo serialNumber ngẫu nhiên
            device.setSerialNumber("SN-" + (random.nextInt(1000000) + 100000));
            // Chọn ngẫu nhiên vendorId từ danh sách vendor
            device.setVendorId(allVendorId.get(random.nextInt(allVendorId.size() - 1)));
            // Chọn ngẫu nhiên loại thiết bị từ DeviceType
            device.setType(deviceTypes[random.nextInt(deviceTypes.length)].ordinal() + 1);
            // Cập nhật ngày tạo và ngày sửa đổi ngẫu nhiên
            Date createAt = new Date(System.currentTimeMillis() - random.nextInt(1000000000)); // Ngày tạo ngẫu nhiên
            device.setCreateDate(createAt);
            device.setLastModified(new Date(createAt.getTime() + random.nextInt(1000000000)));
            deviceService.save(device);
        }
    }
    private void initApiStatusLog(){
        Random random = new Random();
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        String[] urls = {"/api/users", "/api/orders", "/api/products", "/api/payments", "api/liveview"};
        Integer[] statusCodes = {200, 201,400, 404,100, 500,502,503};

        for (int i = 0; i < 10000; i++){
            ApiStatusLog log = new ApiStatusLog();
            log.setId(StringUtil.generateUUID());
            log.setUrl(urls[random.nextInt(urls.length)]);
            log.setMethod(methods[random.nextInt(methods.length)]);
            log.setStatusCode(statusCodes[random.nextInt(statusCodes.length)]);
            log.setDurationTime((long) random.nextInt(5_001));
            apiStatusLogService.save(log);
        }


    }
    @GetMapping("fake-revenue-for-month")
    public ResponseEntity<ResponseObject> fakeRevenue( @RequestParam(value = "month", defaultValue = "3") int month){

        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        int currentYear = LocalDate.now().getYear();
        // Lấy ngày đầu tiên của tháng đó trong năm hiện tại
        // Lấy ngày đầu tiên của tháng được truyền vào
        LocalDate localDate = LocalDate.of(currentYear, month, 1);

        // Chuyển LocalDate thành Date
        Date fakeDate =  Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Random random = new Random();

        List<User> allUser = userService.findAll();

        List<String> allUserId = allUser.stream().map(User::getId).toList();
        List<String> SERVICES = Arrays.asList("record", "cloud");
        Map<String, Long> PRICE_MAP = Map.of(
                "record", 50000L,
                "cloud", 100000L
        );
        List<Long> DURATION_OPTIONS = Arrays.asList(1L, 3L, 7L);
        for (Integer i = 0 ; i < 1000; i++){
            CustomerSubscriber customerSubscriber = new CustomerSubscriber();
            customerSubscriber.setId(StringUtil.generateUUID());
            customerSubscriber.setUserId(allUserId.get(random.nextInt(allUserId.size() - 1)));

            // Chọn service ngẫu nhiên
            String selectedService = SERVICES.get(random.nextInt(SERVICES.size()));
            customerSubscriber.setService(selectedService);

            // Chọn thời gian sử dụng từ danh sách (1, 3, 7 ngày)
            customerSubscriber.setDuration(DURATION_OPTIONS.get(random.nextInt(DURATION_OPTIONS.size())));
            customerSubscriber.setPrice(customerSubscriber.getDuration()*(PRICE_MAP.get(selectedService)));

            customerSubscriber.setCreateDate(fakeDate);
            customerSubscriber.setLastModified(fakeDate);

            customerSubscriberService.save(customerSubscriber);
        }

        return ResponseEntity.ok(responseObject);
    }

}
