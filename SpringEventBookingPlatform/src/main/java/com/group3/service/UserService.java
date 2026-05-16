/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service;

import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.dto.response.UserResponse;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author THUAN
 */
public interface UserService extends UserDetailsService {
    List<UserResponse> getUsers(Map<String, String> params);

    Long countUsers(Map<String, String> params);

    UserResponse getUserById(int id);

    UserResponse getUserByEmail(String email);

    boolean checkExistEmail(String email);
    
//    boolean checkExistUsername(String username);

    void deleteUser(int id);

    Long countUsers();

//    UserResponse getUserByUsername(String username);

    UserResponse addUser(RegisterRequest request, MultipartFile avatar,int roleId);

    UserResponse authenticate(LoginRequest request);
    
    UserResponse updateUser(Integer id, UserUpdateRequest request,MultipartFile avatar);
}
