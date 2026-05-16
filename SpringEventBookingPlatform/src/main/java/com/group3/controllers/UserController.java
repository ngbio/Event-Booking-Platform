package com.group3.controllers;

import com.group3.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author THUAN
 */
@Controller
@RequestMapping("/admin")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/login")
    public String loginView(){
        return "login";
    }
    @GetMapping("/users")
    public String listUsers(Model model, @RequestParam Map<String, String> params) {
       model.addAttribute("users",this.userService.getUsers(params));
       model.addAttribute("activePage", "users");
       return "users";
    }
}
