/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.StatusUser;
import com.group3.pojo.User;

/**
 *
 * @author thanh
 */
public interface StatusUserRepository {

    StatusUser getStatusUserById(Integer id);

    void changeStatusUser(User user);
}
