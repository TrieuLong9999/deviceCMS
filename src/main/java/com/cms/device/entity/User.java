package com.cms.device.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "User")
public class User {
    @Id
    private String id;
    private String name;
    private String phone;
    private Integer type;
    private Date createDate;
    private Date lastModified;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public enum UserType{
        PRE_PAY(1),
        PAY_LATER(2);
        private final int code;  // Mã số (code)

        UserType(int code) {
            this.code = code;
        }
        public int getCode() {
            return code;
        }
        public String getName() {
            return name();  // Trả về tên enum (chẳng hạn: "EVENT_FACE")
        }
        // Tìm kiếm EventType từ code
        public static UserType fromCode(int code) {
            for (User.UserType deviceType : User.UserType.values()) {
                if (deviceType.getCode() == code) {
                    return deviceType;
                }
            }
            return null;
        }
    }
}
