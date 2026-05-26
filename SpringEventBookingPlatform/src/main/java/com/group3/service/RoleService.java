package com.group3.service;

import com.group3.dto.response.RoleResponse;
import java.util.List;
import java.util.Map;

public interface RoleService {
	List<RoleResponse> findAll();
	Map<String,String> getRoles();
}
