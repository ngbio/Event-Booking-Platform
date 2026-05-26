/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.controllers;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.EventResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author thanh
 */
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
    private EventService eventService;
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> listOrganizerEvents(
            Principal principal,
            @RequestParam Map<String, String> params) {
        User organizer = getCurrentOrganizer(principal);

        Map<String, String> filters = new HashMap<>(params);
        filters.put("organizerId", organizer.getId().toString());

        List<EventResponse> events = eventService.getEvents(filters);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách sự kiện của nhà tổ chức thành công", events));
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EventResponse>> createOrganizerEvent(
            Principal principal,
            @Valid @ModelAttribute EventRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video) {
        User organizer = getCurrentOrganizer(principal);
        EventResponse event = eventService.createEvent(request, image, video, organizer);

        return new ResponseEntity<>(new ApiResponse<>(201, "Tạo sự kiện thành công", event), HttpStatus.CREATED);
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
        User organizer = getCurrentOrganizer(principal);
        ensureOwner(id, organizer);

        EventResponse event = eventService.updateEvent(id, request, image, video);
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật sự kiện thành công", event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrganizerEvent(
            Principal principal,
            @PathVariable("id") Integer id) {
        User organizer = getCurrentOrganizer(principal);
        ensureOwner(id, organizer);

        boolean deleted = eventService.deleteEvent(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Xóa sự kiện thành công", null));
    }

    private User getCurrentOrganizer(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        User user = userService.getUserEntityByEmail(principal.getName());
        if (user == null) {
            throw new UnauthorizedException("Không tìm thấy người dùng đăng nhập");
        }
        if (user.getRoleId() == null || user.getRoleId().getId() != 2) {
            throw new UnauthorizedException("Chỉ nhà tổ chức mới thực hiện được thao tác này");
        }

        return user;
    }

    private void ensureOwner(Integer eventId, User organizer) {
        EventResponse event = eventService.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }
        if (!organizer.getId().equals(event.getOrganizerId())) {
            throw new UnauthorizedException("Bạn không có quyền thao tác sự kiện này");
        }
    }
}
