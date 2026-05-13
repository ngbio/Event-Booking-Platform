package com.group3.utils.mapper;

import com.group3.pojo.Booking;
import com.group3.pojo.response.ResBookingDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    /**
     * Convert Booking entity to ResBookingDTO
     */
    public static ResBookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }
        
        ResBookingDTO dto = new ResBookingDTO();
        dto.setId(booking.getId());
        dto.setQuantity(booking.getQuantity());
        dto.setUnitPrice(booking.getUnitPrice());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setActive(booking.getActive());
        dto.setCreatedAt(booking.getCreatedDate());
        dto.setUpdatedAt(booking.getUpdatedDate());
        
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
    public static List<ResBookingDTO> toDTOList(List<Booking> bookings) {
        if (bookings == null) {
            return new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::toDTO)
                .collect(Collectors.toList());
    }
}
