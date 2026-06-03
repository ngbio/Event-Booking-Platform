
package com.group3.repository;

import com.group3.pojo.Role;
import java.util.List;


public interface RoleRepository {
    List<Role> findAll();
    Role findById(Integer id);
}