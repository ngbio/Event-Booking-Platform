/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.Role;
import java.util.List;

/**
 *
 * @author THUAN
 */
public interface RoleRepository {
    List<Role> findAll();
    Role findById(Integer id);
}