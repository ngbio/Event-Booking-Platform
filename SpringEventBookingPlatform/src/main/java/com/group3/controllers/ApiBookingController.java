package com.group3.controllers;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.BookingResponse;
import com.group3.service.BookingService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings(
            Principal principal,
            @RequestParam Map<String, String> params) {
        List<BookingResponse> bookings = this.bookingService.getMyBookings(principal, params);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy lịch sử đặt vé thành cống", bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingDetail(
            Principal principal,
            @PathVariable("id") Integer id) {
        BookingResponse booking = this.bookingService.getBookingDetail(id, principal);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết booking thành công", booking));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Boolean>> cancelBooking(
            Principal principal,
            @PathVariable("id") Integer id) {
        boolean cancelled = this.bookingService.cancelBooking(id, principal);
        return ResponseEntity.ok(new ApiResponse<>(200, "Hủy đơn đặt vé thành công", cancelled));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            Principal principal,
            @Valid @RequestBody BookingRequest request) {
        BookingResponse booking = this.bookingService.createBooking(request, principal);
        return new ResponseEntity<>(new ApiResponse<>(201, "Tạo đơn đặt vé thành công, vui lòng thanh toán trong 10 phút", booking), HttpStatus.CREATED);
    }
}
