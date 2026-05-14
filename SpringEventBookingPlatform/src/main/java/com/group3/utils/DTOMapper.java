package com.group3.utils;

import com.group3.pojo.Booking;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.Payment;
import com.group3.pojo.Role;
import com.group3.pojo.User;
import com.group3.dto.response.BookingResponse;
import com.group3.dto.response.CategoryResponse;
import com.group3.dto.response.EventResponse;
import com.group3.dto.response.PaymentResponse;
import com.group3.dto.response.RoleResponse;
import com.group3.dto.response.UserResponse;
import com.group3.mapper.BookingMapper;
import com.group3.mapper.CategoryMapper;
import com.group3.mapper.EventMapper;
import com.group3.mapper.PaymentMapper;
import com.group3.mapper.RoleMapper;
import com.group3.mapper.UserMapper;
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
    public static UserResponse mapUser(User user) {
        return UserMapper.toResponse(user);
    }

    /**
     * Convert Event entity to ResEventDTO
     */
    public static EventResponse mapEvent(Event event) {
        return EventMapper.toResponse(event);
    }

    /**
     * Convert Category entity to ResCategoryDTO
     */
    public static CategoryResponse mapCategory(Category category) {
        return CategoryMapper.toResponse(category);
    }

    /**
     * Convert Role entity to ResRoleDTO
     */
    public static RoleResponse mapRole(Role role) {
        return RoleMapper.toResponse(role);
    }

    /**
     * Convert Booking entity to ResBookingDTO
     */
    public static BookingResponse mapBooking(Booking booking) {
        return BookingMapper.toResponse(booking);
    }

    /**
     * Convert Payment entity to ResPaymentDTO
     */
    public static PaymentResponse mapPayment(Payment payment) {
        return PaymentMapper.toResponse(payment);
    }

    // ============ List Conversion Methods ============

    /**
     * Convert List of Users to List of ResUserDTOs
     */
    public static List<UserResponse> mapUsers(List<User> users) {
        return UserMapper.toResponseList(users);
    }

    /**
     * Convert List of Events to List of ResEventDTOs
     */
    public static List<EventResponse> mapEvents(List<Event> events) {
        return EventMapper.toResponseList(events);
    }

    /**
     * Convert List of Categories to List of ResCategoryDTOs
     */
    public static List<CategoryResponse> mapCategories(List<Category> categories) {
        return CategoryMapper.toResponseList(categories);
    }

    /**
     * Convert List of Bookings to List of ResBookingDTOs
     */
    public static List<BookingResponse> mapBookings(List<Booking> bookings) {
        return BookingMapper.toResponseList(bookings);
    }

    /**
     * Convert List of Payments to List of ResPaymentDTOs
     */
    public static List<PaymentResponse> mapPayments(List<Payment> payments) {
        return PaymentMapper.toResponseList(payments);
    }

    /**
     * Convert List of Roles to List of ResRoleDTOs
     */
    public static List<RoleResponse> mapRoles(List<Role> roles) {
        return RoleMapper.toResponseList(roles);
    }
}
