package com.group3.mapper;

import com.group3.dto.request.RegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
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

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());
        response.setIdentityCard(user.getIdentityCard());
        response.setOrganizationName(user.getOrganizationName());
        response.setTaxCode(user.getTaxCode());

        if (user.getRoleId() != null) {
            response.setRoleId(user.getRoleId().getId());
            response.setRoleName(user.getRoleId().getName());
        }

        if (user.getStatusId() != null) {
            response.setStatusId(user.getStatusId().getId());
            response.setStatusName(user.getStatusId().getName());
        }

        return response;
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

    //Register
    public static User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setIdentityCard(request.getIdentityCard());
        user.setOrganizationName(request.getOrganizationName());
        user.setTaxCode(request.getTaxCode());

        return user;
    }

    //Update
    public static User toEntity(UserUpdateRequest request, User user) {
        if (request == null || user == null) {
            return user;
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getIdentityCard() != null) {
            user.setIdentityCard(request.getIdentityCard());
        }

        if (request.getOrganizationName() != null) {
            user.setOrganizationName(request.getOrganizationName());
        }

        if (request.getTaxCode() != null) {
            user.setTaxCode(request.getTaxCode());
        }

        return user;
    }
}
