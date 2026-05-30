package com.group3.controllers;

import com.group3.dto.response.UserResponse;
import com.group3.pojo.Organizer;
import com.group3.service.EventService;
import com.group3.service.StatusUserService;
import com.group3.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserController {
//      @RequestMapping("/admin/users")
//Chứa các Routes (Nhóm 8 - Quản lý Người dùng):

    /// (GET: Hiển thị danh sách và phân trang người dùng)
/// /detail (GET: Hiển thị trang xem thông tin chi tiết một tài khoản)
/// /update-status (POST: Form submit Khóa hoạt động hoặc Mở khóa tài khoản)
    @Autowired
    private StatusUserService statusUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventService eventService;

    private static final int ROLE_ORGANIZER = 2;
    private static final int ROLE_ATTENDEE = 3;
    private static final int STATUS_PENDING = 1;
    private static final int STATUS_ACTIVE = 2;
    private static final int STATUS_BANNED = 3;

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
        model.addAttribute("kw", params.getOrDefault("kw", ""));
        model.addAttribute("roleId", params.getOrDefault("roleId", ""));
        model.addAttribute("statusId", params.getOrDefault("statusId", ""));

        return "users";
    }

    @GetMapping("/{id}")
    public String userDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        UserResponse user = userService.getUserById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errMsg", "Không tìm thấy thông tin người dùng!");
            return "redirect:/admin/users";
        }

        model.addAttribute("user", user);
        return "user-detail";
    }

    @PostMapping("/status")
    public String updateUserStatus(@RequestParam("id") Integer id,
            @RequestParam("statusId") Integer statusId,
            RedirectAttributes redirectAttributes) {
        if (id == null || statusId == null) {
            redirectAttributes.addFlashAttribute("errMsg", "Yêu cầu không hợp lệ!");
            return "redirect:/admin/users";
        }
        boolean success = statusUserService.changeStatusUser(id, statusId);
        if (success) {
            if (statusId == STATUS_ACTIVE) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật trạng thái hoạt động thành công!");
            } else if (statusId == STATUS_BANNED) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã khóa tài khoản thành công!");
            }
        } else {
            redirectAttributes.addFlashAttribute("errMsg", "Thao tác thất bại!");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/events")
    public String listOrganizerEvents(@PathVariable("id") Integer userId,
            Model model,
            @RequestParam Map<String, String> params) {
        UserResponse user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("fullName", user.getFullName());
        }
        params.put("organizerId", String.valueOf(userId));
        model.addAttribute("events", eventService.getEvents(params));
        int page = Integer.parseInt(params.getOrDefault("page", "1"));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) eventService.countEvents(params) / 10));
        model.addAttribute("activePage", "users");

        return "events";
    }
}
