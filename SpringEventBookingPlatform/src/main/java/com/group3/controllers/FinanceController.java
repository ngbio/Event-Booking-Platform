package com.group3.controllers;

import com.group3.dto.response.EventResponse;
import com.group3.pojo.Event;
import com.group3.service.BookingService;
import com.group3.service.EventService;
import java.util.List;
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
@RequestMapping("/admin/finance")
public class FinanceController {

    @Autowired
    private EventService eventService;

    @Autowired
    private BookingService bookingService;

    private static final int BOOKING_REFUNDING = 3;
    private static final int BOOKING_REFUNDED = 4;

    @GetMapping("/pending-refund")
    public String pendingRefund(Model model) {
        model.addAttribute("events", eventService.getEventsForRefund());
        model.addAttribute("activePage", "finance");
        return "pending-refund";
    }

    @PostMapping("/approve-refund")
    public String approveRefund(@RequestParam("id") Integer eventId, RedirectAttributes redirectAttributes) {
        try {
            int updatedCount = bookingService.updateStatusByEventId(eventId, BOOKING_REFUNDING, BOOKING_REFUNDED);

            if (updatedCount > 0) {
                redirectAttributes.addFlashAttribute("successMsg", "Thành công! Đã hoàn trả tiền cho toàn bộ " + updatedCount + " đơn hàng mua vé sự kiện.");
            } else {
                redirectAttributes.addFlashAttribute("errMsg", "Không tìm thấy vé nào ở trạng thái hợp lệ để hoàn tiền.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Lỗi  hoàn tiền.");
        }
        return "redirect:/admin/finance/pending-refund";
    }

    @GetMapping("/settlements")
    public String settlements(Model model) {
        model.addAttribute("events", eventService.getEventsForSettlement());
        model.addAttribute("activePage", "finance");
        return "settlements";
    }

    @PostMapping("/process-settlement")
    public String processSettlement(@RequestParam("id") Integer eventId, RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMsg", "Đã phê duyệt và chuyển tiền quyết toán thành công sang tài khoản Nhà tổ chức!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "Thao tác xử lý quyết toán dòng tiền thất bại.");
        }
        return "redirect:/admin/finance/settlements";
    }

    @GetMapping("/events/{eventId}/bookings")
    public String viewEventBookings(@PathVariable("eventId") Integer eventId,
            @RequestParam Map<String, String> params,
            Model model) {
        EventResponse event = eventService.getEventById(eventId);
        if (event == null) {
            return "redirect:/admin/finance/pending-refund";
        }

        model.addAttribute("event", event);
        model.addAttribute("bookings", bookingService.getBookingsByEventId(eventId, params));
        model.addAttribute("activePage", "finance");
        return "finance-event-bookings";
    }

}
