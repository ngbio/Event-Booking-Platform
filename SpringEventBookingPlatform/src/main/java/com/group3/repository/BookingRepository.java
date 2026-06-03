package com.group3.repository;

import com.group3.pojo.Booking;
import java.util.List;
import java.util.Map;

public interface BookingRepository {

    Booking addBooking(Booking booking);

    Booking getBookingById(Integer id);

    List<Booking> getBookingsByUserId(Integer userId, Map<String, String> params);

    List<Booking> getBookingsByEventId(Integer eventId, Map<String, String> params);

    Booking updateBooking(Booking booking);

    int updateStatusByEventId(Integer eventId, Integer oldStatusId, Integer newStatusId);

}
