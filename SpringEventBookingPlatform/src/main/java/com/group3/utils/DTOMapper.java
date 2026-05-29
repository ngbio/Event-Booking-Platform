package com.group3.utils;

import com.group3.dto.request.AttendeeRegisterRequest;
import com.group3.dto.request.CategoryRequest;
import com.group3.dto.request.EventRequest;
import com.group3.dto.request.OrganizerRegisterRequest;
import com.group3.dto.request.UserUpdateRequest;
import com.group3.pojo.Booking;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.Payment;
import com.group3.pojo.Role;
import com.group3.pojo.User;
import com.group3.dto.response.BookingResponse;
import com.group3.dto.response.CategoryResponse;
import com.group3.dto.response.EventFeeResponse;
import com.group3.dto.response.EventResponse;
import com.group3.dto.response.PaymentResponse;
import com.group3.dto.response.RoleResponse;
import com.group3.dto.response.TicketResponse;
import com.group3.dto.response.UserResponse;
import com.group3.mapper.BookingMapper;
import com.group3.mapper.CategoryMapper;
import com.group3.mapper.EventFeeMapper;
import com.group3.mapper.EventMapper;
import com.group3.mapper.PaymentMapper;
import com.group3.mapper.RoleMapper;
import com.group3.mapper.TicketMapper;
import com.group3.mapper.UserMapper;
import com.group3.pojo.EventFee;
import com.group3.pojo.TicketDetail;
import java.util.List;

public class DTOMapper {

    public static UserResponse toUserResponse(User user) {
        return UserMapper.toResponse(user);
    }

    public static EventResponse toEventResponse(Event event) {
        return EventMapper.toResponse(event);
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryMapper.toResponse(category);
    }

    public static RoleResponse toRoleResponse(Role role) {
        return RoleMapper.toResponse(role);
    }
 
    public static BookingResponse toBookingResponse(Booking booking) {
        return BookingMapper.toResponse(booking);
    }

    public static PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentMapper.toResponse(payment);
    }

    public static TicketResponse toTicketResponse(TicketDetail ticket) {
        return TicketMapper.toResponse(ticket);
    }

    public static List<UserResponse> toUserResponseList(List<User> users) {
        return UserMapper.toResponseList(users);
    }

    public static List<EventResponse> toEventResponseList(List<Event> events) {
        return EventMapper.toResponseList(events);
    }

    public static List<CategoryResponse> toCategoryResponseList(List<Category> categories) {
        return CategoryMapper.toResponseList(categories);
    }

    public static List<BookingResponse> toBookingResponseList(List<Booking> bookings) {
        return BookingMapper.toResponseList(bookings);
    }

    public static List<PaymentResponse> toPaymentResponseList(List<Payment> payments) {
        return PaymentMapper.toResponseList(payments);
    }

    public static List<TicketResponse> toTicketResponseList(List<TicketDetail> tickets) {
        return TicketMapper.toResponseList(tickets);
    }

    public static List<RoleResponse> toRoleResponseList(List<Role> roles) {
        return RoleMapper.toResponseList(roles);
    }
    
    public static Event toEventEntity(EventRequest request){
        return EventMapper.toEntity(request);
    }
    
    public static Event toEventEntity(EventRequest request, Event existingEvent){
        return EventMapper.toEntity(request, existingEvent);
    }
    
    public static User toUserEntity(OrganizerRegisterRequest request){
        return UserMapper.toEntity(request);
    }
    public static User toUserEntity(AttendeeRegisterRequest request){
        return UserMapper.toEntity(request);
    }
    
    public static User toUserEntity(UserUpdateRequest request, User user){
        return UserMapper.toEntity(request, user);
    }
    
    public static Category toCategoryEntity(CategoryRequest request){
        return CategoryMapper.toEntity(request);
    }
    
    public static EventFeeResponse toEventFeeResponse(EventFee fee){
        return EventFeeMapper.toResponse(fee);
    }
    
    public static List<EventFeeResponse> toEventFeeResponseList(List<EventFee> fees){
        return EventFeeMapper.toResponseList(fees);
    }
}
