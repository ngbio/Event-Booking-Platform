package com.group3.utils.mapper;

import com.group3.pojo.User;
import com.group3.pojo.response.ResUserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    /**
     * Convert User entity to ResUserDTO
     */
    public static ResUserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        
        ResUserDTO dto = new ResUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getRoleId() != null) {
            dto.setRoleId(user.getRoleId().getId());
            dto.setRoleName(user.getRoleId().getName());
        }
        
        return dto;
    }

    /**
     * Convert List of Users to List of ResUserDTOs
     */
    public static List<ResUserDTO> toDTOList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
}
