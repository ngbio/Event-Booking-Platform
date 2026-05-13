package com.group3.service;

import com.group3.pojo.Event;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    List<Event> getEvents(Map<String, String> params);
    Event getEventById(Integer id);
    Event createEvent(Map<String, String> params, MultipartFile image, MultipartFile video, User organizer);
    Event updateEvent(Integer id, Map<String, String> params, MultipartFile image, MultipartFile video);
    boolean deleteEvent(Integer id);
    int getAvailableTickets(Integer eventId);
    boolean updateTicketsAfterBooking(Integer eventId, int quantityBooked);
}
