/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author thanh
 */
public class CategoryRequest {
    @NotBlank(message="ID không được trống")
    private Integer id;
    @NotBlank(message="Tên danh mục không được trống")
    private String name;
    @NotBlank(message="Active không được trống")
    private boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
}
