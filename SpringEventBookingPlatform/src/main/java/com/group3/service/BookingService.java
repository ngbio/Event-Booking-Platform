package com.group3.service;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.BookingResponse;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request, User attendee);

    List<BookingResponse> getMyBookings(User attendee, Map<String, String> params);

    BookingResponse getBookingDetail(Integer bookingId, User currentUser);

    boolean cancelBooking(Integer bookingId, User attendee);

    List<BookingResponse> getEventBookings(Integer eventId, User organizer, Map<String, String> params);
}
