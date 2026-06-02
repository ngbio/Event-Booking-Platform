package com.group3.service;

import com.group3.dto.response.RoleResponse;
import java.util.List;
import java.util.Map;

// UNUSED_BY_CURRENT_CODE: no controller/service currently injects or calls RoleService.
public interface RoleService {
	List<RoleResponse> findAll();
	Map<String,String> getRoles();
}
