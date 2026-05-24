/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.controllers;

import com.group3.dto.response.EventResponse;
import com.group3.service.EventService;
import com.group3.service.StatusEventService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author thanh
 */
@Controller
@RequestMapping("/admin/events")
public class EventAdminController {

    @Autowired
    private EventService eventService;
    @Autowired
    private StatusEventService statusEventService;

    @GetMapping
    public String listEvents(Model model, @RequestParam Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1"));
        int pageSize = 5; 

        model.addAttribute("events", eventService.getEvents(params));

        long totalEvents = eventService.countEvents(params);
        int totalPages = (int) Math.ceil((double) totalEvents / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "events");
        model.addAttribute("searchBy", params.getOrDefault("searchBy", "title"));

        return "events";
    }

    @PostMapping("/delete")
    public String deleteEvent(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean isDeleted = this.eventService.deleteEvent(id);
            if (isDeleted) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã xóa sự kiện thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Xóa thất bại! Không tìm thấy sự kiện.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Không thể xóa! Sự kiện này đã có giao dịch vé.");
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/approve")
    public String approveEvent(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean isChanged = this.statusEventService.changeStatusEvent(id, 3);
            if (isChanged) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã duyệt và mở bán sự kiện thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Duyệt thất bại! Không tìm thấy sự kiện.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Lỗi hệ thống khi duyệt sự kiện!");
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/cancel")
    public String cancelEvent(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean isChanged = this.statusEventService.changeStatusEvent(id, 4);
            if (isChanged) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã hủy thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Hủy thất bại, vui lòng thử lại!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Lỗi! Không thể hủy sự kiện này.");
        }
        return "redirect:/admin/events";
    }
    
    @GetMapping("/detail")
    public String eventDetail(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        EventResponse event = eventService.getEventById(id);
        if (event == null) {
            redirectAttributes.addFlashAttribute("errMsg", "Không tìm thấy sự kiện!");
            return "redirect:/admin/events";
        }
        model.addAttribute("event", event);
        return "event-detail"; 
    }
}
