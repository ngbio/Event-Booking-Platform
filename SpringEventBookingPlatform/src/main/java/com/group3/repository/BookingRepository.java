package com.group3.repository;

import com.group3.pojo.Booking;
import java.util.List;
import java.util.Map;

public interface BookingRepository {

    Booking addBooking(Booking booking);

    Booking getBookingById(Integer id);

    List<Booking> getBookingsByUserId(Integer userId, Map<String, String> params);

    List<Booking> getBookingsByEventId(Integer eventId, Map<String, String> params);

    List<Booking> getBookingsByOrganizer(Integer organizerId, Map<String, String> params);

    Booking updateBooking(Booking booking);

    long countBookingsByUserId(Integer userId, Map<String, String> params);

    long countBookingsByEventId(Integer eventId, Map<String, String> params);

    boolean existsPaidBooking(Integer eventId, Integer userId);
    
    int updateStatusByEventId(Integer eventId, Integer oldStatusId, Integer newStatusId);

}
