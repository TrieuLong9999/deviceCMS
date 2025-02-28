package com.cms.device.dto.response;

import com.cms.device.util.StaticCode;

import java.util.List;

public class ResponseListObject {
    private Integer code;
    private String message;
    private Object data;
    private Long page;
    private Long pageSize;
    private Long total;

    public ResponseListObject() {
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

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
    public  void setSuccess(Object data){
        this.code = StaticCode.CodeResponse.Success.getCode();
        this.message = StaticCode.CodeResponse.Success.getName();
        this.data = data;
    }

    public void setCodeResponse(StaticCode.CodeResponse codeStatus){
        this.code = codeStatus.getCode();
        this.message = codeStatus.getName();
    }

}
