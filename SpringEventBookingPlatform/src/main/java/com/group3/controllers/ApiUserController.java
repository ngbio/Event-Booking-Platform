package com.group3.controllers;

import com.group3.service.UserService;
import com.group3.utils.JwtUtils;
import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.LoginResponse;
import com.group3.dto.response.UserResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class ApiUserController {

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

    @PostMapping("/secure/logout")
    public ResponseEntity<?> logout(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chua dang nhap hoac token het han");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Dang xuat thanh cong"));
    }

    @GetMapping("/secure/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        UserResponse user = userService.getUserByEmail(principal.getName());

        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin người dùng thành công", user));

    }
}
