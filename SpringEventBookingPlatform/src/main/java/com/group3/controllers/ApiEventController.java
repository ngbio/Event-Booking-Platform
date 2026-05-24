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
import org.springframework.web.bind.annotation.CrossOrigin;
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

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class ApiEventController {

    private static final String OPEN_FOR_SALE_STATUS = "3";

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EventResponse>>> listPublicEvents(@RequestParam Map<String, String> params) {
        Map<String, String> filters = new HashMap<>(params);
        filters.put("statusId", OPEN_FOR_SALE_STATUS);

        List<EventResponse> events = eventService.getEvents(filters);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay danh sach su kien thanh cong", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getPublicEvent(@PathVariable("id") Integer id) {
        EventResponse event = eventService.getEventById(id);
        if (event == null || !Integer.valueOf(OPEN_FOR_SALE_STATUS).equals(event.getStatusId())) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Lay thong tin su kien thanh cong", event));
    }

    @GetMapping("/{id}/available-tickets")
    public ResponseEntity<ApiResponse<Integer>> getAvailableTickets(@PathVariable("id") Integer id) {
        EventResponse event = eventService.getEventById(id);
        if (event == null || !Integer.valueOf(OPEN_FOR_SALE_STATUS).equals(event.getStatusId())) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }

        int availableTickets = eventService.getAvailableTickets(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay so ve con lai thanh cong", availableTickets));
    }

    @GetMapping("/organizer/my-events")
    public ResponseEntity<ApiResponse<List<EventResponse>>> listOrganizerEvents(
            Principal principal,
            @RequestParam Map<String, String> params) {
        User organizer = getCurrentOrganizer(principal);

        Map<String, String> filters = new HashMap<>(params);
        filters.put("organizerId", organizer.getId().toString());

        List<EventResponse> events = eventService.getEvents(filters);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay danh sach su kien cua nha to chuc thanh cong", events));
    }

    @PostMapping(path = "/organizer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<EventResponse>> createOrganizerEvent(
            Principal principal,
            @Valid @ModelAttribute EventRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video) {
        User organizer = getCurrentOrganizer(principal);
        EventResponse event = eventService.createEvent(request, image, video, organizer);

        return new ResponseEntity<>(new ApiResponse<>(201, "Tao su kien thanh cong", event), HttpStatus.CREATED);
    }

    @PutMapping(path = "/organizer/{id}",
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
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Cap nhat su kien thanh cong", event));
    }

    @DeleteMapping("/organizer/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrganizerEvent(
            Principal principal,
            @PathVariable("id") Integer id) {
        User organizer = getCurrentOrganizer(principal);
        ensureOwner(id, organizer);

        boolean deleted = eventService.deleteEvent(id);
        if (!deleted) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Xoa su kien thanh cong", null));
    }

    private User getCurrentOrganizer(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chua dang nhap hoac token het han");
        }

        User user = userService.getUserEntityByEmail(principal.getName());
        if (user == null) {
            throw new UnauthorizedException("Khong tim thay nguoi dung dang nhap");
        }
        if (user.getRoleId() == null || user.getRoleId().getId() != 2) {
            throw new UnauthorizedException("Chi nha to chuc moi duoc thuc hien thao tac nay");
        }

        return user;
    }

    private void ensureOwner(Integer eventId, User organizer) {
        EventResponse event = eventService.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }
        if (!organizer.getId().equals(event.getOrganizerId())) {
            throw new UnauthorizedException("Ban khong co quyen thao tac tren su kien nay");
        }
    }
}
