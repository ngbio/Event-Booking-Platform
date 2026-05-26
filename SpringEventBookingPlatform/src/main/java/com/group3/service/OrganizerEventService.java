package com.group3.service;

import com.group3.dto.request.EventRequest;
import com.group3.dto.response.BookingResponse;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.group3.dto.response.EventResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizerEventService {

    List<EventResponse> getOrganizerEvents(Principal principal, Map<String, String> params);

    EventResponse createOrganizerEvent(Principal principal, EventRequest request, MultipartFile image, MultipartFile video);

    EventResponse getOrganizerEventDetail(Principal principal, Integer eventId);

    EventResponse updateOrganizerEvent(Principal principal, Integer eventId, EventRequest request, MultipartFile image, MultipartFile video);

    void changeOrganizerEventStatus(Principal principal, Integer eventId, Integer statusId);

//    List<BookingResponse> getEventBookings(Principal principal, Integer eventId, Map<String, String> params);
}
