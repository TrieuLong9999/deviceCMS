package com.cms.device.controller;

import com.cms.device.dto.response.ResponseListObject;
import com.cms.device.dto.response.ResponseObject;
import com.cms.device.entity.Vendor;
import com.cms.device.serviceLayer.service.VendorService;
import com.cms.device.util.StaticCode;
import com.cms.device.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vendor")
public class VendorController {
    @Autowired
    private VendorService vendorService;
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
}
