package com.group3.controllers;

import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.TicketResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.User;
import com.group3.service.TicketService;
import com.group3.service.UserService;
import java.security.Principal;
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
@RequestMapping("/api/secure/tickets")
@CrossOrigin
public class ApiTicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(
            Principal principal,
            @RequestParam Map<String, String> params) {
        User attendee = getCurrentUser(principal);
        List<TicketResponse> tickets = this.ticketService.getMyTickets(attendee, params);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay danh sach ve thanh cong", tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketDetail(
            Principal principal,
            @PathVariable("id") Integer id) {
        User attendee = getCurrentUser(principal);
        TicketResponse ticket = this.ticketService.getTicketDetail(id, attendee);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay chi tiet ve thanh cong", ticket));
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
