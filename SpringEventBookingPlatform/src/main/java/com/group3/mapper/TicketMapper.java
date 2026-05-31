package com.group3.mapper;

import com.group3.dto.response.TicketResponse;
import com.group3.pojo.Booking;
import com.group3.pojo.Event;
import com.group3.pojo.TicketDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketMapper {

    public static TicketResponse toResponse(TicketDetail ticket) {
        if (ticket == null) {
            return null;
        }

        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setQrCode(ticket.getQrCode());
        response.setCreatedDate(ticket.getCreatedDate());
        response.setUpdatedDate(ticket.getUpdatedDate());

        Booking booking = ticket.getBookingId();
        if (booking != null) {
            response.setBookingId(booking.getId());
            response.setUnitPrice(booking.getUnitPrice());

            if (booking.getAttendeeId() != null && booking.getAttendeeId().getUser() != null) {
                response.setUserId(booking.getAttendeeId().getUser().getId());
                response.setEmail(booking.getAttendeeId().getUser().getEmail());
            }

            Event event = booking.getEventId();
            if (event != null) {
                response.setEventId(event.getId());
                response.setEventTitle(event.getTitle());
                response.setEventLocation(event.getLocation());
                response.setEventStartTime(event.getStartTime());
                response.setEventEndTime(event.getEndTime());
            }
        }

        if (ticket.getStatusId() != null) {
            response.setStatusId(ticket.getStatusId().getId());
            response.setStatusName(ticket.getStatusId().getName());
        }

        return response;
    }

    public static List<TicketResponse> toResponseList(List<TicketDetail> tickets) {
        if (tickets == null) {
            return new ArrayList<>();
        }
        return tickets.stream()
                .map(TicketMapper::toResponse)
                .collect(Collectors.toList());
    }
}
