package com.group3.controllers;

import com.group3.service.StatsService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class HomeController {


    
    @Autowired
    private StatsService statsService;
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        Map<String, Object> stats = statsService.getDashboardStats();
        model.addAllAttributes(stats);
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
