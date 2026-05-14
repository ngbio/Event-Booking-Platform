package com.group3.mapper;

import com.group3.pojo.User;
import com.group3.dto.response.UserResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    /**
     * Convert User entity to ResUserDTO
     */
    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setActive(user.getActive());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdatedDate(user.getUpdatedDate());
        
        if (user.getRoleId() != null) {
            dto.setRoleId(user.getRoleId().getId());
            dto.setRoleName(user.getRoleId().getName());
        }
        
        return dto;
    }

    /**
     * Convert List of Users to List of ResUserDTOs
     */
    public static List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }
}
