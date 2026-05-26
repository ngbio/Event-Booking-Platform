/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author thanh
 */
@Controller
@RequestMapping("/admin")
public class LoginAdmin {
//      @RequestMapping("/admin")
//Chứa các Routes (Nhóm 8 - Đăng nhập Admin):
/// /login (GET: Hiển thị trang đăng nhập dành riêng cho Admin)
    @GetMapping("/login")
    public String loginView(){
        return "login";
    }
}
