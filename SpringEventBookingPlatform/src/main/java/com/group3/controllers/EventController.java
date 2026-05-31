package com.group3.controllers;

import com.group3.dto.response.EventResponse;
import com.group3.service.BookingService; // THÊM IMPORT NÀY
import com.group3.service.EventService;
import com.group3.service.StatusEventService;
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
@RequestMapping("/admin/events")
public class EventController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private StatusEventService statusEventService;

    @Autowired
    private BookingService bookingService;

    private static final int STATUS_PENDING = 1;
    private static final int STATUS_PUBLISHED = 2;
    private static final int STATUS_CANCELLED = 5;
    private static final int BOOKING_PAID = 2;
    private static final int BOOKING_REFUNDING = 3;

    @GetMapping
    public String listEvents(Model model, @RequestParam Map<String, String> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1"));
        int pageSize = 10;

        model.addAttribute("events", eventService.getEvents(params));

        long totalEvents = eventService.countEvents(params);
        int totalPages = (int) Math.ceil((double) totalEvents / pageSize);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("activePage", "events");
        model.addAttribute("kw", params.getOrDefault("kw", ""));
        model.addAttribute("searchBy", params.getOrDefault("searchBy", "title"));
        model.addAttribute("statusId", params.getOrDefault("statusId", ""));

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
            EventResponse event = this.eventService.getEventByIdForAdmin(id);
            if (event == null) {
                redirectAttributes.addFlashAttribute("errMsg", "Sự kiện không tồn tại!");
                return "redirect:/admin/events";
            }
            if (event.getStatusId() != STATUS_PENDING) {
                redirectAttributes.addFlashAttribute("errMsg", "Chỉ có thể duyệt sự kiện đang ở trạng thái 'Chờ Duyệt'!");
                return "redirect:/admin/events";
            }
            boolean isChanged = this.statusEventService.changeStatusEvent(id, STATUS_PUBLISHED);

            if (isChanged) {
                redirectAttributes.addFlashAttribute("successMsg", "Đã duyệt và mở bán sự kiện thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Có lỗi xảy ra trong quá trình duyệt!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:/admin/events";
    }

    @PostMapping("/cancel")
    public String cancelEvent(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        try {
            EventResponse event = this.eventService.getEventByIdForAdmin(id);
            if (event == null) {
                redirectAttributes.addFlashAttribute("errMsg", "Sự kiện không tồn tại!");
                return "redirect:/admin/events";
            }
            if (event.getStatusId() != STATUS_PENDING && event.getStatusId() != STATUS_PUBLISHED) {
                redirectAttributes.addFlashAttribute("errMsg", "Chỉ có thể hủy khi sự kiện ở trạng thái 'Chờ duyệt' hoặc 'Đang mở bán'");
                return "redirect:/admin/events";
            }
            boolean isChanged = this.statusEventService.changeStatusEvent(id, STATUS_CANCELLED);
            
            if (isChanged) {
                int updatedBookings = bookingService.updateStatusByEventId(id, BOOKING_PAID, BOOKING_REFUNDING);
                
                redirectAttributes.addFlashAttribute("successMsg", "Đã hủy sự kiện và tự động chuyển " + updatedBookings + " vé sang trạng thái chờ hoàn tiền!");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Có lỗi xảy ra trong quá trình hủy!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:/admin/events";
    }

    @GetMapping("/{id}")
    public String eventDetail(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        EventResponse event = eventService.getEventByIdForAdmin(id);
        if (event == null) {
            redirectAttributes.addFlashAttribute("errMsg", "Sự kiện không tồn tại!");
            return "redirect:/admin/events";
        }
        model.addAttribute("event", event);
        return "event-detail";
    }
}