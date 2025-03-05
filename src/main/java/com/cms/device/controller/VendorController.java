package com.cms.device.controller;

import com.cms.device.dto.BaseFormRequest;
import com.cms.device.dto.response.ResponseListObject;
import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.Device;
import com.cms.device.entity.Vendor;
import com.cms.device.serviceLayer.service.DeviceService;
import com.cms.device.serviceLayer.service.VendorService;
import com.cms.device.util.StaticCode;
import com.cms.device.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/vendor")
public class VendorController {
    @Autowired
    private VendorService vendorService;
    @Autowired
    private DeviceService deviceService;
    @GetMapping("getAll")
    public ResponseEntity<ResponseObject> getAllVendor(){
        List<Vendor> vendorList = vendorService.getAll();
        ResponseObject responseListObject = new ResponseObject();
        responseListObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseListObject.setData(vendorList);
        return ResponseEntity.ok(responseListObject);
    }
    @PostMapping("add")
    public ResponseEntity<ResponseObject> addVendor(@RequestBody Vendor vendor){
        ResponseObject responseObject = new ResponseObject();

        if(vendor == null){
            responseObject.setCodeResponse(StaticCode.CodeResponse.Invalid_Form_Request);
            return ResponseEntity.ok(responseObject);
        }
        if(vendorService.getByName(vendor.getName()) != null){
            responseObject.setCodeResponse(StaticCode.CodeResponse.Exist_Data);
            return ResponseEntity.ok(responseObject);
        }
        if(StringUtil.isEmpty(vendor.getId())){
            vendor.setId(StringUtil.generateUUID());
        }

        vendorService.save(vendor);
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        responseObject.setData(vendor);
        return ResponseEntity.ok(responseObject);
    }

    @GetMapping("count-device-by-vendor")
    public ResponseEntity<ResponseObject> countDeviceByVendor(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);
        List<Vendor> vendorList = vendorService.getAll();
        Map<String, String> mapVendor = vendorList.stream().collect(Collectors.toMap(
                Vendor::getId, Vendor::getShortName
        ));
        List<Map<String, Object>> datas = deviceService.getDeviceStatsByVendor();
        List<Map<String, Object>> dataResponse = new ArrayList<>();

        for(Map<String, Object> tempData : datas){
            if(tempData.get("_id") != null){
                String idVendor = (String) tempData.get("_id");

                if (mapVendor.get(idVendor) != null){
                    tempData.put("vendorName",(mapVendor.get(idVendor)));
                    dataResponse.add(tempData);
                }
            }
        }
        responseObject.setData(dataResponse);
        return ResponseEntity.ok(responseObject);
    }
    @GetMapping("/type-distribution-device")
    public ResponseEntity<ResponseObject> typeDeviceDistribution(){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        List<Vendor> vendorList = vendorService.getAll();
        List<String> vendorIds = vendorList.stream().map(Vendor::getId).toList();
//        System.out.println(vendorIds);
        List<Map<String, Object>> datas = deviceService.getDeviceTypeDistributionByVendors(vendorIds);

        List<Map<String, Object>> dataResponse = new ArrayList<>();

        for(Map<String, Object> tempData: datas){
            if(tempData.get("_id") != null){
                try{
                    Integer type = Integer.parseInt(tempData.get("_id").toString());
                    String nameType = Device.DeviceType.fromCode(type).getName();
                    if(!StringUtil.isEmpty(nameType)){
                        tempData.put("nameType", nameType);
                        dataResponse.add(tempData);
                    }
                }catch (Exception e) {

                }
            }

        }
        responseObject.setData(dataResponse);
        return ResponseEntity.ok(responseObject);

    }
    @GetMapping("/new-devices-days")
    public ResponseEntity<ResponseObject> newDeviceByDay(@RequestParam("day") int day){
        ResponseObject responseObject = new ResponseObject();
        responseObject.setCodeResponse(StaticCode.CodeResponse.Success);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, - day); // Lấy ngày 7 ngày trước
        Date fromDate = calendar.getTime();

        responseObject.setData(deviceService.getNewDevicesDays(fromDate));
        return ResponseEntity.ok(responseObject);
    }

    @PostMapping("vendor-and-device")
    public ResponseEntity<ResponseListObject> vendorAndDevice(@RequestBody BaseFormRequest formRequest){
        ResponseListObject responseListObject = new ResponseListObject();

        responseListObject.setCodeResponse(StaticCode.CodeResponse.Success);
        if(formRequest.validate()) return  ResponseEntity.ok(responseListObject);
        List<Map<String, Object>> data = deviceService.getVendorDeviceStats(formRequest.toPageable());

        responseListObject.setData(data);
        responseListObject.setPage((long)formRequest.getPage());
        responseListObject.setPageSize((long)formRequest.getPageSize());

        responseListObject.setTotal(deviceService.getTotalVendorDeviceStats());

        return ResponseEntity.ok(responseListObject);
    }
}
