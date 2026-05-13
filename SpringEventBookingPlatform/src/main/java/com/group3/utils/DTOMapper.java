package com.group3.utils;

import com.group3.pojo.Booking;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.Payment;
import com.group3.pojo.Role;
import com.group3.pojo.User;
import com.group3.pojo.response.ResBookingDTO;
import com.group3.pojo.response.ResCategoryDTO;
import com.group3.pojo.response.ResEventDTO;
import com.group3.pojo.response.ResPaymentDTO;
import com.group3.pojo.response.ResRoleDTO;
import com.group3.pojo.response.ResUserDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DTOMapper {

    /**
     * Convert User entity to ResUserDTO
     */
    public static ResUserDTO toUserDTO(User user) {
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
     * Convert Event entity to ResEventDTO
     */
    public static ResEventDTO toEventDTO(Event event) {
        if (event == null) {
            return null;
        }
        
        ResEventDTO dto = new ResEventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setImageUrl(event.getImageUrl());
        dto.setVideoUrl(event.getVideoUrl());
        dto.setLocation(event.getLocation());
        dto.setTotalTickets(event.getTotalTickets());
        dto.setPrice(event.getPrice());
        dto.setActive(event.getActive());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        
        if (event.getOrganizerId() != null) {
            dto.setOrganizerId(event.getOrganizerId().getId());
            dto.setOrganizerName(event.getOrganizerId().getFullName());
        }
        
        if (event.getCategoryCollection() != null && !event.getCategoryCollection().isEmpty()) {
            Category firstCat = event.getCategoryCollection().iterator().next();
            if (firstCat != null) {
                dto.setCategoryId(firstCat.getId());
                dto.setCategoryName(firstCat.getName());
            }
        }
        
        return dto;
    }

    /**
     * Convert Category entity to ResCategoryDTO
     */
    public static ResCategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return new ResCategoryDTO(
            category.getId(),
            category.getName(),
            category.getActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    /**
     * Convert Role entity to ResRoleDTO
     */
    public static ResRoleDTO toRoleDTO(Role role) {
        if (role == null) {
            return null;
        }
        
        return new ResRoleDTO(role.getId(), role.getName());
    }

    /**
     * Convert Booking entity to ResBookingDTO
     */
    public static ResBookingDTO toBookingDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        ResBookingDTO dto = new ResBookingDTO();
        dto.setId(booking.getId());
        dto.setQuantity(booking.getQuantity());
        dto.setUnitPrice(booking.getUnitPrice());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setActive(booking.getActive());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        if (booking.getEventId() != null) {
            dto.setEventId(booking.getEventId().getId());
            dto.setEventTitle(booking.getEventId().getTitle());
        }
        
        if (booking.getUserId() != null) {
            dto.setUserId(booking.getUserId().getId());
            dto.setUsername(booking.getUserId().getUsername());
        }
        
        if (booking.getStatusId() != null) {
            dto.setStatusId(booking.getStatusId().getId());
            // Note: Statusbooking entity should have getName() method
        }
        
        return dto;
    }

    /**
     * Convert Payment entity to ResPaymentDTO
     */
    public static ResPaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        ResPaymentDTO dto = new ResPaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setActive(payment.getActive());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        
        if (payment.getBookingId() != null) {
            dto.setBookingId(payment.getBookingId().getId());
        }
        
        if (payment.getUserId() != null) {
            dto.setUserId(payment.getUserId().getId());
            dto.setUsername(payment.getUserId().getUsername());
        }
        
        if (payment.getStatusId() != null) {
            dto.setStatusId(payment.getStatusId().getId());
            // Note: Statuspay entity should have getName() method
        }
        
        return dto;
    }

    // ============ List Conversion Methods ============

    /**
     * Convert List of Users to List of ResUserDTOs
     */
    public static List<ResUserDTO> toUserDTOList(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(DTOMapper::toUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of Events to List of ResEventDTOs
     */
    public static List<ResEventDTO> toEventDTOList(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(DTOMapper::toEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of Categories to List of ResCategoryDTOs
     */
    public static List<ResCategoryDTO> toCategoryDTOList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories.stream()
                .map(DTOMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of Bookings to List of ResBookingDTOs
     */
    public static List<ResBookingDTO> toBookingDTOList(List<Booking> bookings) {
        if (bookings == null) {
            return new ArrayList<>();
        }
        return bookings.stream()
                .map(DTOMapper::toBookingDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of Payments to List of ResPaymentDTOs
     */
    public static List<ResPaymentDTO> toPaymentDTOList(List<Payment> payments) {
        if (payments == null) {
            return new ArrayList<>();
        }
        return payments.stream()
                .map(DTOMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert List of Roles to List of ResRoleDTOs
     */
    public static List<ResRoleDTO> toRoleDTOList(List<Role> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(DTOMapper::toRoleDTO)
                .collect(Collectors.toList());
    }
}
