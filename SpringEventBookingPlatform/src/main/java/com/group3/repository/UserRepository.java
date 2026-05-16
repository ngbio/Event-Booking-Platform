/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;
import com.group3.dto.response.UserResponse;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;

/**
 *
 * @author THUAN
 */
public interface UserRepository {
    List<User> getUsers(Map<String, String> params);

    Long countUsers(Map<String, String> params);

    User findUserById(Integer id);

    User findUserByEmail(String email);

    boolean existEmail(String email);

    void addOrUpdateUser(User u);

    void deleteUser(Integer id);

    Long count();

    User addUser(User u);
        
    boolean authenticate(String email, String password);
}
