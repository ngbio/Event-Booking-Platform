/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service;

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
    List<User> getUsers(Map<String, String> params);

    Long countUsers(Map<String, String> params);

    void addOrUpdateUser(User u);

    User getUserById(int id);

    User getUserByEmail(String email);

    boolean checkExistEmail(String email);
    
    boolean checkExistUsername(String username);

    void deleteUser(int id);

    Long countUsers();

    User getUserByUsername(String username);

    User addUser(Map<String, String> params, MultipartFile avatar);

    boolean authenticate(String username, String password);

}
