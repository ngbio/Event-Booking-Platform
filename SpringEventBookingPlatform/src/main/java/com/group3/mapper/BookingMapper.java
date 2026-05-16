package com.group3.mapper;

import com.group3.pojo.Booking;
import com.group3.dto.response.BookingResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    /**
     * Convert Booking entity to ResBookingDTO
     */
    public static BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setQuantity(booking.getQuantity());
        response.setUnitPrice(booking.getUnitPrice());
        response.setTotalPrice(booking.getTotalPrice());
        response.setCreatedDate(booking.getCreatedDate());
        response.setUpdatedDate(booking.getUpdatedDate());
        
        if (booking.getEventId() != null) {
            response.setEventId(booking.getEventId().getId());
            response.setEventTitle(booking.getEventId().getTitle());
        }
        
        if (booking.getUserId() != null) {
            response.setUserId(booking.getUserId().getId());
            response.setEmail(booking.getUserId().getEmail());
        }
        
        if (booking.getStatusId() != null) {
            response.setStatusId(booking.getStatusId().getId());
            // Note: Statusbooking entity should have getName() method
        }
        
        return response;
    }

    /**
     * Convert List of Bookings to List of ResBookingDTOs
     */
    public static List<BookingResponse> toResponseList(List<Booking> bookings) {
        if (bookings == null) {
            return new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }
}
