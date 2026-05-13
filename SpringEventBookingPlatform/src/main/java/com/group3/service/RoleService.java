/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service;

import com.group3.pojo.Role;
import java.util.List;
import java.util.Map;

/**
 *
 * @author THUAN
 */
public interface RoleService {
	List<Role> findAll();
	Map<String,String> getRoles();
}
