package com.cms.device.serviceLayer.service;

import com.cms.device.entity.*;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntityStructureService {
    public List<String> getEntityFields(Class<?> entityClass) {
        List<String> fields = new ArrayList<>();

        // Duyệt qua tất cả các trường trong entity
        for (Field field : entityClass.getDeclaredFields()) {
            fields.add(field.getName() + " : " + field.getType().getSimpleName());
        }

        return fields;
    }
    // Phương thức này lấy thông tin cho tất cả các entity bạn muốn render
    public Map<String,List<String>> getAllEntitiesStructure() {
        Map<String,List<String>> entitiesStructure = new HashMap<>();

        // Lấy các entity classes mà bạn muốn render (có thể hard-code hoặc tự động phát hiện các lớp)
        entitiesStructure.put(CustomerSubscriber.class.getSimpleName(),getEntityFields(CustomerExperience.class));
        entitiesStructure.put(CustomerSubscriber.class.getSimpleName(),getEntityFields(CustomerSubscriber.class));
        entitiesStructure.put(Device.class.getSimpleName(),getEntityFields(Device.class));
        entitiesStructure.put(Feedback.class.getSimpleName(),getEntityFields(Feedback.class));
        entitiesStructure.put(User.class.getSimpleName(),getEntityFields(User.class));
        entitiesStructure.put(Vendor.class.getSimpleName(),getEntityFields(Vendor.class));

        return entitiesStructure;
    }


}
