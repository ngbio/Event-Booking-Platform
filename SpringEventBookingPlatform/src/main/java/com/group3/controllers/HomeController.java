package com.group3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {
//      @RequestMapping("/admin")
//Chứa các Routes (Nhóm 8 - Dashboard Admin):
/// / (GET: Hiển thị giao diện Dashboard thống kê tổng quan sàn)
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
