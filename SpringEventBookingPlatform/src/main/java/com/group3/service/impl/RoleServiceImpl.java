package com.group3.service.impl;

import com.group3.dto.response.RoleResponse;
import com.group3.pojo.Role;
import com.group3.repository.RoleRepository;
import com.group3.service.RoleService;
import com.group3.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// UNUSED_BY_CURRENT_CODE: RoleService is not injected/called anywhere in the current backend.
@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<RoleResponse> findAll() {
            List<Role> roles = this.roleRepository.findAll();
		return DTOMapper.toRoleResponseList(roles);
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
