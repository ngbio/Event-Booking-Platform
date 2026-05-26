package com.group3.service;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.EventResponse;
import com.group3.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

    List<EventResponse> getEvents(Map<String, String> params);

    EventResponse getEventById(Integer id);

    EventResponse createEvent(EventRequest request, MultipartFile image, MultipartFile video, User organizer);

    EventResponse updateEvent(Integer id, EventRequest request, MultipartFile image, MultipartFile video);

    boolean deleteEvent(Integer id);

    int getAvailableTickets(Integer eventId);

    boolean updateTicketsAfterBooking(Integer eventId, int quantityBooked);

    Long countEvents(Map<String, String> params);

    List<EventResponse> getEventsByIds(List<Integer> EventIds);
}
