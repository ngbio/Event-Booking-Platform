/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.controllers;

import com.group3.pojo.User;
import com.group3.service.UserService;
import com.group3.utils.JwtUtils;
import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.response.LoginResponse;
import com.group3.dto.response.RegisterResponse;
import com.group3.dto.response.UserResponse;
import com.group3.mapper.UserMapper;
import java.security.Principal;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

/**
 *
 * @author THUAN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @PostMapping(path = "/users", 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@ModelAttribute RegisterRequest request,
                                       @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            // Check if username or email already exists
            if (userService.checkExistUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên đăng nhập đã tồn tại");
            }

            if (userService.checkExistEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email đã tồn tại");
            }
            
            // Create user through service
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPhone(request.getPhone());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword());

            User savedUser = userService.addUser(newUser, avatar);
            
            // Create and return RegisterResponse
            RegisterResponse response = new RegisterResponse();
            response.setUserId(savedUser.getId());
            response.setUsername(savedUser.getUsername());
            if (savedUser.getRoleId() != null) {
                response.setRole(savedUser.getRoleId().getName());
            }
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi đăng ký: " + e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
                try {
                    User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
                    if (user == null) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
                    }

                    String token = JwtUtils.generateToken(loginRequest.getUsername());
                    
                    
                    LoginResponse response = new LoginResponse();
                    response.setToken(token);
                    response.setUserId(user.getId());
                    response.setUsername(user.getUsername());
                    if (user.getRoleId() != null) {
                        response.setRole(user.getRoleId().getName());
                    }
                    
                    return ResponseEntity.ok().body(response);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo JWT: " + e.getMessage());
                }
    }

    @RequestMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<?> getProfile(Principal principal) {
        try {

            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Chưa đăng nhập");
            }

            User user = userService.getUserByUsername(principal.getName());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tìm thấy");
            }

            UserResponse response = UserMapper.toResponse(user);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi lấy thông tin profile: " + e.getMessage());
        }
    }
}
