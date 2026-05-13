package com.group3.service;

import com.group3.pojo.Role;
import java.util.List;
import java.util.Map;

public interface RoleService {
	List<Role> findAll();
	Map<String,String> getRoles();
}
