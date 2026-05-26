package com.group3.controllers;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.EventResponse;
import com.group3.exceptions.BusinessException;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.User;
import com.group3.service.EventService;
import com.group3.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class ApiEventController {
//      @RequestMapping("/api/events")
//Chứa các API (Nhóm 2 - Public Event - Dành cho khách vãng lai):

    /// (GET: Lấy danh sách sự kiện PUBLISHED)
/// /{id} (GET: Xem chi tiết sự kiện)
/// /{id}/available (GET: Xem số lượng vé còn lại)
/// /compare (GET: So sánh các sự kiện)
    private static final String OPEN_FOR_SALE_STATUS = "2";

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> listPublicEvents(@RequestParam Map<String, String> params) {
        Map<String, String> filters = new HashMap<>(params);
        filters.put("statusId", OPEN_FOR_SALE_STATUS);

        List<EventResponse> events = eventService.getEvents(filters);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách sự kiện thành công", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getPublicEvent(@PathVariable("id") Integer id) {
        EventResponse event = eventService.getEventById(id);
        if (event == null || !Integer.valueOf(OPEN_FOR_SALE_STATUS).equals(event.getStatusId())) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin sự kiện thành công", event));
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<ApiResponse<Integer>> getAvailableTickets(@PathVariable("id") Integer id) {
        EventResponse event = eventService.getEventById(id);
        if (event == null || !Integer.valueOf(OPEN_FOR_SALE_STATUS).equals(event.getStatusId())) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }

        int availableTickets = eventService.getAvailableTickets(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin số vé còn lại thành công", availableTickets));
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compareEvents(@RequestParam List<Integer> eventIds) {
        // Validate: Ví dụ người dùng chọn nhiều hơn 3 sự kiện thì báo lỗi
        if (eventIds == null || eventIds.size() < 2 || eventIds.size() > 3) {
            throw new BusinessException("Vui lòng chọn từ 2 đến 3 sự kiện để so sánh!");
        }

        List<EventResponse> events = eventService.getEventsByIds(eventIds);
        return ResponseEntity.ok(new ApiResponse<>(200, "So sánh sự kiện thành công", events));
    }
}
