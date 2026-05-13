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
import com.group3.utils.mapper.BookingMapper;
import com.group3.utils.mapper.CategoryMapper;
import com.group3.utils.mapper.EventMapper;
import com.group3.utils.mapper.PaymentMapper;
import com.group3.utils.mapper.RoleMapper;
import com.group3.utils.mapper.UserMapper;
import java.util.List;

/**
 * DTOMapper - Facade/Delegator class
 * This class delegates to individual mapper classes
 * Each entity has its own mapper for better organization and maintainability
 */
public class DTOMapper {

    /**
     * Convert User entity to ResUserDTO
     */
    public static ResUserDTO toUserDTO(User user) {
        return UserMapper.toDTO(user);
    }

    /**
     * Convert Event entity to ResEventDTO
     */
    public static ResEventDTO toEventDTO(Event event) {
        return EventMapper.toDTO(event);
    }

    /**
     * Convert Category entity to ResCategoryDTO
     */
    public static ResCategoryDTO toCategoryDTO(Category category) {
        return CategoryMapper.toDTO(category);
    }

    /**
     * Convert Role entity to ResRoleDTO
     */
    public static ResRoleDTO toRoleDTO(Role role) {
        return RoleMapper.toDTO(role);
    }

    /**
     * Convert Booking entity to ResBookingDTO
     */
    public static ResBookingDTO toBookingDTO(Booking booking) {
        return BookingMapper.toDTO(booking);
    }

    /**
     * Convert Payment entity to ResPaymentDTO
     */
    public static ResPaymentDTO toPaymentDTO(Payment payment) {
        return PaymentMapper.toDTO(payment);
    }

    // ============ List Conversion Methods ============

    /**
     * Convert List of Users to List of ResUserDTOs
     */
    public static List<ResUserDTO> toUserDTOList(List<User> users) {
        return UserMapper.toDTOList(users);
    }

    /**
     * Convert List of Events to List of ResEventDTOs
     */
    public static List<ResEventDTO> toEventDTOList(List<Event> events) {
        return EventMapper.toDTOList(events);
    }

    /**
     * Convert List of Categories to List of ResCategoryDTOs
     */
    public static List<ResCategoryDTO> toCategoryDTOList(List<Category> categories) {
        return CategoryMapper.toDTOList(categories);
    }

    /**
     * Convert List of Bookings to List of ResBookingDTOs
     */
    public static List<ResBookingDTO> toBookingDTOList(List<Booking> bookings) {
        return BookingMapper.toDTOList(bookings);
    }

    /**
     * Convert List of Payments to List of ResPaymentDTOs
     */
    public static List<ResPaymentDTO> toPaymentDTOList(List<Payment> payments) {
        return PaymentMapper.toDTOList(payments);
    }

    /**
     * Convert List of Roles to List of ResRoleDTOs
     */
    public static List<ResRoleDTO> toRoleDTOList(List<Role> roles) {
        return RoleMapper.toDTOList(roles);
    }
}
