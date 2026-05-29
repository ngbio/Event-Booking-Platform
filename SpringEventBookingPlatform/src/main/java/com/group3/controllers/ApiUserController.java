package com.group3.controllers;

import com.group3.dto.request.ChangePasswordRequest;
import com.group3.service.UserService;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.UserResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class ApiUserController {
//      @RequestMapping("/api/users/secure")
//Chứa các API (Nhóm 1 - Hồ sơ cá nhân):

    /// /profile (GET: Lấy thông tin cá nhân)
/// /profile (PATCH: Cập nhật hồ sơ/avatar)
/// /password (PATCH: Thay đổi mật khẩu)
    @Autowired
    private UserService userService;

    @GetMapping("/secure/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        UserResponse user = this.userService.getCurrentUserProfile(principal);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin người dùng thành công", user));
    }

    @PatchMapping(path = "/secure/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProfile(Principal principal,
            @Valid @ModelAttribute UserUpdateRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        UserResponse updateUser = userService.updateProfile(principal, request, avatar);
        return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thông tin thành công", updateUser));
    }

    @PatchMapping("/secure/password")
    public ResponseEntity<?> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal, request);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đổi mật khẩu thành công", null));
    }
}
