package com.group3.mapper;

import com.group3.dto.request.AttendeeRegisterRequest;
import com.group3.dto.request.OrganizerRegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.pojo.User;
import com.group3.dto.response.UserResponse;
import com.group3.pojo.Attendee;
import com.group3.pojo.Organizer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    private static final int ROLE_ATTENDEE = 3;
    private static final int ROLE_ORGANIZER = 2;
    
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
        if (user.getAttendee() != null) {
            response.setBirthDate(user.getAttendee().getBirthDate());
            response.setGender(user.getAttendee().getGender());
        }
        if (user.getOrganizer() != null) {
            response.setIdentityCard(user.getOrganizer().getIdentityCard());
            response.setOrganizationName(user.getOrganizer().getOrganizationName());
            response.setTaxCode(user.getOrganizer().getTaxCode());
        }

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

    public static List<UserResponse> toResponseList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static User toEntity(OrganizerRegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        Organizer organizer = new Organizer();
        organizer.setUser(user);
        organizer.setIdentityCard(request.getIdentityCard());
        organizer.setTaxCode(request.getTaxCode());
        organizer.setOrganizationName(request.getOrganizationName());
        organizer.setUser(user);

        user.setOrganizer(organizer);
        return user;
    }

    public static User toEntity(AttendeeRegisterRequest request) {
        if (request == null) {
            return null;
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        Attendee attendee = new Attendee();
        attendee.setBirthDate(request.getBirthDate());
        attendee.setGender(request.getGender());
        attendee.setUser(user);
        user.setAttendee(attendee);
        return user;
    }

    public static User toEntity(UserUpdateRequest request, User user) {
        if (request == null || user == null) {
            return user;
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (user.getRoleId() != null) {
            if (user.getRoleId().getId() == ROLE_ORGANIZER) {
                Organizer organizer = user.getOrganizer();
                if (organizer == null) {
                    organizer = new Organizer();
                    organizer.setUser(user);
                    user.setOrganizer(organizer);
                }
                if (request.getIdentityCard() != null) {
                    organizer.setIdentityCard(request.getIdentityCard());
                }
                if (request.getOrganizationName() != null) {
                    organizer.setOrganizationName(request.getOrganizationName());
                }
                if (request.getTaxCode() != null) {
                    organizer.setTaxCode(request.getTaxCode());
                }
            } else if (user.getRoleId().getId() == ROLE_ATTENDEE) {
                Attendee attendee = user.getAttendee();
                if (attendee == null) {
                    attendee = new Attendee();
                    attendee.setUser(user);
                    user.setAttendee(attendee);
                }
                if (request.getBirthDate() != null) {
                    attendee.setBirthDate(request.getBirthDate());
                }
                if (request.getGender() != null) {
                    attendee.setGender(request.getGender());
                }
            }
        }
        return user;
    }
}
