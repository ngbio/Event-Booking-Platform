package com.group3.pojo.response;

public class ResRoleDTO {
    private Integer id;
    private String name;

    public ResRoleDTO() {
    }

    public ResRoleDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
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
}
