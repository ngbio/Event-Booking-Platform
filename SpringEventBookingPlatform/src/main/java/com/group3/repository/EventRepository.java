package com.group3.repository;

import com.group3.pojo.Event;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EventRepository {
    List<Event> getEvents(Map<String, String> params);
    List<Event> getEventsByIds(List<Integer> EventIds);
    Event getEventById(Integer id);
    Event addEvent(Event event);
    Event updateEvent(Event event);
    boolean deleteEvent(Integer id);
    List<Event> findByCategory(Integer categoryId);
    List<Event> findByParams(Map<String, String> params);
    long countEvents(Map<String, String> params);
    int updateExpiredPublishedEvents(Integer publishedStatusId, Integer completedStatusId, Date now);
    
    List<Event> getEventsForRefund();
    List<Event> getEventsForSettlement();
  
}
