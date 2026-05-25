package com.group3.repository;

import com.group3.pojo.Booking;
import java.util.List;
import java.util.Map;

public interface BookingRepository {
    Booking addBooking(Booking booking);
    Booking getBookingById(Integer id);
    List<Booking> getBookingsByUser(Integer userId, Map<String, String> params);
    List<Booking> getBookingsByEvent(Integer eventId, Map<String, String> params);
    List<Booking> getBookingsByOrganizer(Integer organizerId, Map<String, String> params);
    Booking updateBooking(Booking booking);
    long countBookingsByUser(Integer userId, Map<String, String> params);
    long countBookingsByEvent(Integer eventId, Map<String, String> params);
    boolean existsPaidBooking(Integer eventId, Integer userId);
}
