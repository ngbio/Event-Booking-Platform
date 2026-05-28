package com.group3.controllers;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.BookingResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.User;
import com.group3.service.BookingService;
import com.group3.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure/bookings")
@CrossOrigin
public class ApiBookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            Principal principal,
            @RequestParam Map<String, String> params) {
        User attendee = getCurrentUser(principal);
        List<BookingResponse> bookings = this.bookingService.getMyBookings(attendee, params);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay lich su dat ve thanh cong", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingDetail(
            Principal principal,
            @PathVariable("id") Integer id) {
        User currentUser = getCurrentUser(principal);
        BookingResponse booking = this.bookingService.getBookingDetail(id, currentUser);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay chi tiet booking thanh cong", booking));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Boolean>> cancelBooking(
            Principal principal,
            @PathVariable("id") Integer id) {
        User attendee = getCurrentUser(principal);
        boolean cancelled = this.bookingService.cancelBooking(id, attendee);
        return ResponseEntity.ok(new ApiResponse<>(200, "Huy don dat ve thanh cong", cancelled));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            Principal principal,
            @Valid @RequestBody BookingRequest request) {
        User attendee = getCurrentUser(principal);
        BookingResponse booking = this.bookingService.createBooking(request, attendee);
        return new ResponseEntity<>(new ApiResponse<>(201, "Tao don dat ve thanh cong, vui long thanh toan trong 10 phut", booking), HttpStatus.CREATED);
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chua dang nhap hoac token het han");
        }

        User user = this.userService.getUserEntityByEmail(principal.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Khong tim thay nguoi dung");
        }

        return user;
    }
}
