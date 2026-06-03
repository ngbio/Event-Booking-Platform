package com.group3.service;

import com.group3.dto.response.EventRefundResponse;
import com.group3.dto.response.EventResponse;
import com.group3.dto.response.EventSettlementResponse;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventResponse> getEvents(Map<String, String> params);

    EventResponse getEventById(Integer eventId);

    boolean deleteEvent(Integer eventId);

    int getAvailableTickets(Integer eventId);

    Long countEvents(Map<String, String> params);

    List<EventResponse> getEventsByIds(List<Integer> EventIds);
    
    EventResponse getEventByIdForAdmin(Integer evenId);
    
    List<EventRefundResponse> getEventsForRefund();
    
    List<EventSettlementResponse> getEventsForSettlement();
    
    boolean processSettlement(Integer eventId, String settlementCode);
}
