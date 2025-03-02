package com.cms.device.dto;

import com.cms.device.util.StringUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BaseFormRequest {
    private int page = 0;
    private int pageSize = 10;
    private String sortBy;
    private String sortOder;


    public BaseFormRequest() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOder() {
        return sortOder;
    }

    public void setSortOder(String sortOder) {
        this.sortOder = sortOder;
    }

    // Chuyển đổi BasePageableForm thành Pageable để sử dụng trong truy vấn
    public Pageable toPageable() {
        if (StringUtil.isEmpty(sortBy) || StringUtil.isEmpty(sortOder)) {
            return PageRequest.of(page, pageSize);
        }else {
            return PageRequest.of(page, pageSize,
                    "desc".equalsIgnoreCase(sortOder) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        }
    }
}
