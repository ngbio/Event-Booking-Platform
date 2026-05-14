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
        
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setQuantity(booking.getQuantity());
        dto.setUnitPrice(booking.getUnitPrice());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setActive(booking.getActive());
        dto.setCreatedDate(booking.getCreatedDate());
        dto.setUpdatedDate(booking.getUpdatedDate());
        
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
