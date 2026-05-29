package com.group3.service;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.BookingResponse;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request, Principal principal);

    List<BookingResponse> getMyBookings(Principal principal, Map<String, String> params);

    BookingResponse getBookingDetail(Integer bookingId, Principal principal);

    boolean cancelBooking(Integer bookingId, Principal principal);

}
