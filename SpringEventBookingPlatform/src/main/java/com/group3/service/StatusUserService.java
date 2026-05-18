/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service;

import com.group3.pojo.StatusUser;

/**
 *
 * @author thanh
 */
public interface StatusUserService {
    StatusUser getStatusUserById(Integer id);

    boolean changeStatusUser(Integer userId, Integer statusId);
}
