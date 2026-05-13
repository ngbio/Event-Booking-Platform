/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.controllers;

import com.group3.pojo.User;
import com.group3.pojo.Role;
import com.group3.pojo.StatusUser;
import com.group3.pojo.request.ReqLoginDTO;
import com.group3.pojo.request.ReqRegisterDTO;
import com.group3.pojo.response.ResLoginDTO;
import com.group3.pojo.response.ResRegisterDTO;
import com.group3.pojo.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.group3.utils.JwtUtils;
import com.group3.utils.IdInvalidException;
import com.group3.repository.RoleRepository;

import jakarta.validation.Valid;

import com.group3.service.UserService;
import org.springframework.http.HttpStatus;
import java.util.Date;

/**
 *
 * @author THUAN
 */
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<RestResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO request) throws Exception {
        User user = userService.getUserByUsername(request.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("Tên đăng nhập không tồn tại");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không đúng");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());

        String roleName = user.getRoleId() != null ? user.getRoleId().getName() : "USER";
        ResLoginDTO resLogin = new ResLoginDTO(accessToken, user.getId(), user.getUsername(), roleName);

        RestResponse<ResLoginDTO> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.OK.value());
        res.setMessage("Login thành công");
        res.setData(resLogin);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<RestResponse<ResRegisterDTO>> register(@Valid @RequestBody ReqRegisterDTO request)
            throws IdInvalidException {

        if (userService.checkExistEmail(request.getEmail())) {
            throw new IdInvalidException("Email đã được sử dụng");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreatedDate(new Date());
        user.setActive(true);
        
        // Set default role (USER - id: 2) and status (ACTIVE - id: 1)
        Role userRole = roleRepository.findById(2); // Assuming id 2 is USER role
        user.setRoleId(userRole != null ? userRole : new Role(2));
        user.setStatusId(new StatusUser(1)); // Assuming id 1 is ACTIVE status
        
        userService.addOrUpdateUser(user);

        User saved = userService.getUserByUsername(request.getUsername());
        
        String roleName = saved.getRoleId() != null ? saved.getRoleId().getName() : "USER";
        ResRegisterDTO resRegister = new ResRegisterDTO(saved.getId(), saved.getUsername(), roleName);

        RestResponse<ResRegisterDTO> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.CREATED.value());
        res.setMessage("Register thành công");
        res.setData(resRegister);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}
