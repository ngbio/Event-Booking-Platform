package com.group3.controllers;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.BookingResponse;
import com.group3.dto.response.EventResponse;
import com.group3.service.EventService;
import com.group3.service.OrganizerEventService;
import com.group3.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secure/organizer/events")
public class ApiOrganizerEventController {
//      @RequestMapping("/api/secure/organizer/events")
//Chứa các API (Nhóm 3 - Đặc quyền của Nhà tổ chức):
/// (GET: Danh sách sự kiện của mình, POST: Tạo sự kiện mới)
/// /{id} (GET: Xem chi tiết, PUT: Cập nhật toàn bộ)
/// /{id}/status (PATCH: Tạm ẩn hoặc Yêu cầu hủy sự kiện)
/// /{id}/bookings (GET: Xem danh sách khách mua vé của sự kiện)
    @Autowired
    private OrganizerEventService organizerEventService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> listOrganizerEvents(
            Principal principal,
            @RequestParam Map<String, String> params) {
            
        List<EventResponse> events = organizerEventService.getOrganizerEvents(principal, params);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách sự kiện thành công", events));
    }
    
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EventResponse>> createOrganizerEvent(
            Principal principal,
            @Valid @ModelAttribute EventRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video) {
            
        EventResponse event = organizerEventService.createOrganizerEvent(principal, request, image, video);
        return new ResponseEntity<>(new ApiResponse<>(201, "Tạo sự kiện thành công", event), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getOrganizerEvent(
            Principal principal,
            @PathVariable("id") Integer id) {
        EventResponse eventDetail = organizerEventService.getOrganizerEvent(principal, id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết sự kiện thành công", eventDetail));
    }

    @PutMapping(path = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EventResponse>> updateOrganizerEvent(
            Principal principal,
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute EventRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video) {
            
        EventResponse event = organizerEventService.updateOrganizerEvent(principal, id, request, image, video);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật sự kiện thành công", event));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Object>> changeEventStatus(
            Principal principal,
            @PathVariable("id") Integer id,
            @RequestParam("statusId") Integer statusId) { 
            // Dùng @RequestParam cho lẹ (ví dụ gửi lên /status?statusId=3)
            // Hoặc có thể tạo EventStatusRequest DTO và dùng @RequestBody nếu muốn truyền JSON
            
        organizerEventService.changeOrganizerEventStatus(principal, id, statusId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật trạng thái sự kiện thành công", null));
    }

//    @GetMapping("/{id}/bookings")
//    public ResponseEntity<ApiResponse<List<BookingResponse>>> getEventBookings(
//            Principal principal,
//            @PathVariable("id") Integer id,
//            @RequestParam Map<String, String> params) { 
//            
//        List<BookingResponse> bookings = organizerEventService.getEventBookings(principal, id, params);
//        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách khách đặt vé thành công", bookings));
//    }
}
