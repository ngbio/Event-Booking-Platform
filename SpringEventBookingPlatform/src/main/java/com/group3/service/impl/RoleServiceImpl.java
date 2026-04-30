/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service.impl;

import com.group3.pojo.Role;
import com.group3.repository.RoleRepository;
import com.group3.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author THUAN
 */
@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public Map<String, String> getRoles() {
		Map<String, String> roleTerm = new HashMap<>();
		List<Role> roles = roleRepository.findAll();
		for (Role role : roles) {
			roleTerm.put(role.getId().toString(), role.getName());
		}
		return roleTerm;
	}
}
