package com.group3.mapper;

import com.group3.pojo.Role;
import com.group3.dto.response.RoleResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoleMapper {

    /**
     * Convert Role entity to ResRoleDTO
     */
    public static RoleResponse toDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        return new RoleResponse(role.getId(), role.getName());
    }

    /**
     * Convert List of Roles to List of ResRoleDTOs
     */
    public static List<RoleResponse> toDTOList(List<Role> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }
}
