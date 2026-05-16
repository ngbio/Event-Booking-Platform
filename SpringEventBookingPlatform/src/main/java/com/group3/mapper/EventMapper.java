package com.group3.mapper;

import com.group3.dto.request.EventRequest;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.dto.response.EventResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    /**
     * Convert Event entity to ResEventDTO
     */
    public static EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }
        
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setImageUrl(event.getImageUrl());
        response.setVideoUrl(event.getVideoUrl());
        response.setLocation(event.getLocation());
        response.setTotalTickets(event.getTotalTickets());
        response.setPrice(event.getPrice());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setCreatedDate(event.getCreatedDate());
        response.setUpdatedDate(event.getUpdatedDate());
        response.setSoldTickets(event.getSoldTickets());
        response.setIsPaidFee(event.getIsPaidFee());
        response.setListingFee(event.getListingFee());
        response.setSettlementCode(event.getSettlementCode());
        response.setStatusId(event.getStatusId().getId());
        response.setStatusName(event.getStatusId().getName());
        
        if (event.getOrganizerId() != null) {
            response.setOrganizerId(event.getOrganizerId().getId());
            response.setOrganizerName(event.getOrganizerId().getFullName());
        }
        
        if (event.getCategoryCollection() != null && !event.getCategoryCollection().isEmpty()) {
            Category firstCat = event.getCategoryCollection().iterator().next();
            if (firstCat != null) {
                response.setCategoryId(firstCat.getId());
                response.setCategoryName(firstCat.getName());
            }
        }
        
        return response;
    }

    /**
     * Convert List of Events to List of ResEventDTOs
     */
    public static List<EventResponse> toResponseList(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
                .map(EventMapper::toResponse)
                .collect(Collectors.toList());
    }
    public static Event toEntity(EventRequest request){
        if (request==null) return null;
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setTotalTickets(request.getTotalTickets());
        event.setPrice(request.getPrice());
        event.setSoldTickets(0);
        event.setSettlementCode(null);
        return event;
    }
}
