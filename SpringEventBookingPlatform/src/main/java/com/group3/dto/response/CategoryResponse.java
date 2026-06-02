package com.group3.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

public class CategoryResponse {
    private Integer id;
    private String name;
    private Boolean active;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;

    public CategoryResponse() {
    }

    public CategoryResponse(Integer id, String name, Boolean active, Date createdDate, Date updatedDate) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.createdDate = createdDate;
        this.updatedDate= updatedDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
