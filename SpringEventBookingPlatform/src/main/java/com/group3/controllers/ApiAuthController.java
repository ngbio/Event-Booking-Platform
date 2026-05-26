package com.group3.controllers;

import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.LoginResponse;
import com.group3.dto.response.UserResponse;
import com.group3.exceptions.UnauthorizedException;
import com.group3.service.UserService;
import com.group3.utils.JwtUtils;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
//      @RequestMapping("/api/auth")
//Chứa các API (Nhóm 1 - Xác thực):
/// /login (POST: Đăng nhập hệ thống)
/// /register (POST: Đăng ký tài khoản Attendee/Organizer)
/// /logout (POST: Đăng xuất)
    @Autowired
    private UserService userService;

    @PostMapping(path = "/register/attendee",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAttendee(@Valid @ModelAttribute RegisterRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        UserResponse savedUser = userService.addUser(request, avatar, 3);
        return new ResponseEntity<>(new ApiResponse<>(201, "Đăng ký thành công", savedUser), HttpStatus.CREATED);
    }

    @PostMapping(path = "/register/organizer",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerOrganizer(@Valid @ModelAttribute RegisterRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        UserResponse savedUser = userService.addUser(request, avatar, 2);
        return new ResponseEntity<>(new ApiResponse<>(201, "Đăng ký thành công", savedUser), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        UserResponse user = userService.authenticate(request);
        if (user == null) {
            throw new UnauthorizedException("Sai email hoặc mật khẩu");
        }

        String token = JwtUtils.generateToken(user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        if (user.getRoleName() != null) {
            response.setRole(user.getRoleName());
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng nhập thành công", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng xuất thành công"));
    }
}
