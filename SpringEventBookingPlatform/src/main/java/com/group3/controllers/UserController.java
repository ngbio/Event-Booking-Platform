package com.group3.controllers;

import com.group3.dto.response.UserResponse;
import com.group3.service.StatusUserService;
import com.group3.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author THUAN
 */
@Controller
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private StatusUserService statusUserService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model, @RequestParam Map<String, String> params) {
        model.addAttribute("users", userService.getUsers(params));

        long totalUsers = userService.countUsers(params);
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        int page = Integer.parseInt(params.getOrDefault("page", "1"));

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "users");
        model.addAttribute("selectedRole", params.getOrDefault("roleId", ""));

        return "users";
    }

    @GetMapping("/detail")
    public String userDetail(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        UserResponse user = userService.getUserById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errMsg", "Không tìm thấy thông tin người dùng!");
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "user-detail";
    }

    @PostMapping("/update-status")
    public String updateUserStatus(@RequestParam("id") Integer id,
            @RequestParam("statusId") Integer statusId,
            RedirectAttributes redirectAttributes) {

        boolean success = statusUserService.changeStatusUser(id, statusId);
        if (success) {
            if (statusId == 2) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật trạng thái hoạt động thành công!");
            } else if (statusId == 3) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã khóa tài khoản thành công!");
            }
        } else {
            redirectAttributes.addFlashAttribute("errMsg", "Thao tác thất bại!");
        }
        return "redirect:/admin/users";
    }
}
