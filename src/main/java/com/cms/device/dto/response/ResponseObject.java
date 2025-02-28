package com.cms.device.dto.response;

import com.cms.device.util.StaticCode;

public class ResponseObject {
    private Integer code;
    private String message;
    private Object data;

    public ResponseObject() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public void setCodeResponse(StaticCode.CodeResponse codeStatus){
        this.code = codeStatus.getCode();
        this.message = codeStatus.getName();
    }
}
