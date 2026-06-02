package com.group3.controllers;

import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.EventResponse;
import com.group3.service.EventService;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
@Validated
public class ApiEventController {
    @Autowired
    private EventService eventService;
    private static final String PUBLISHED_STATUS_ID = "2";

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> listPublicEvents(@RequestParam Map<String, String> params) {
        Map<String, String> filters = new HashMap<>(params);
        filters.put("statusId", PUBLISHED_STATUS_ID);
        filters.put("activeOnly", "true");
        List<EventResponse> events = eventService.getEvents(filters);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách sự kiện thành công", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getPublicEvent(@PathVariable("id") Integer eventId) {
        EventResponse event = eventService.getEventById(eventId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin sự kiện thành công", event));
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<ApiResponse<Integer>> getAvailableTickets(@PathVariable("id") Integer eventId) {
        int availableTickets = eventService.getAvailableTickets(eventId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin số vé còn lại thành công", availableTickets));
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compareEvents(@RequestParam
            @Size(min = 2, max = 3, message = "Chọn từ 2 đến 3 sự kiện để so sánh") List<Integer> eventIds) {
        List<EventResponse> events = eventService.getEventsByIds(eventIds);
        return ResponseEntity.ok(new ApiResponse<>(200, "So sánh sự kiện thành công", events));
    }
}
