package com.cms.device.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Device")
public class Device {
    @Id
    private String id;
    private String model;
    private String name;
    private String serialNumber;
    private String vendorId;
    private Date createDate;
    private Integer type;
    private Date lastModified;

    public enum DeviceType{
        SMART_CAMERA(1),
        SMART_LIGHT(2),
        SMART_DOOR(3),
        CAMERA_WIFI(4);
        private final int code;  // Mã số (code)

        DeviceType(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public String getName() {
            return name();  // Trả về tên enum (chẳng hạn: "EVENT_FACE")
        }
        // Tìm kiếm EventType từ code
        public static DeviceType fromCode(int code) {
            for (DeviceType deviceType : DeviceType.values()) {
                if (deviceType.getCode() == code) {
                    return deviceType;
                }
            }
            return null;
        }
    }

    public Device() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
