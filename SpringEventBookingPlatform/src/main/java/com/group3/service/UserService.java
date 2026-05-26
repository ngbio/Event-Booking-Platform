package com.group3.service;

import com.group3.dto.request.LoginRequest;
import com.group3.dto.request.RegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.dto.request.ChangePasswordRequest;
import com.group3.dto.response.UserResponse;
import com.group3.pojo.User;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    List<UserResponse> getUsers(Map<String, String> params);

    Long countUsers(Map<String, String> params);

    UserResponse getUserById(int id);

    UserResponse getUserByEmail(String email);

    User getUserEntityByEmail(String email);

    boolean checkExistEmail(String email);

    void deleteUser(int id);

    Long countUsers();

    UserResponse addUser(RegisterRequest request, MultipartFile avatar,int roleId);

    UserResponse authenticate(LoginRequest request);
    
    UserResponse updateUser(Integer id, UserUpdateRequest request,MultipartFile avatar);
    
    void changePassword(Principal principal, ChangePasswordRequest request);
}
