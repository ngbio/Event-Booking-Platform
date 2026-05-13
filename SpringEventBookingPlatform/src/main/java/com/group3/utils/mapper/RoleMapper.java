package com.group3.utils.mapper;

import com.group3.pojo.Role;
import com.group3.pojo.response.ResRoleDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoleMapper {

    /**
     * Convert Role entity to ResRoleDTO
     */
    public static ResRoleDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        return new ResRoleDTO(role.getId(), role.getName());
    }

    /**
     * Convert List of Roles to List of ResRoleDTOs
     */
    public static List<ResRoleDTO> toDTOList(List<Role> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }
}
