package com.group3.mapper;

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
        
        EventResponse dto = new EventResponse();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setImageUrl(event.getImageUrl());
        dto.setVideoUrl(event.getVideoUrl());
        dto.setLocation(event.getLocation());
        dto.setTotalTickets(event.getTotalTickets());
        dto.setPrice(event.getPrice());
        dto.setActive(event.getActive());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setCreatedDate(event.getCreatedDate());
        dto.setUpdatedDate(event.getUpdatedDate());
        
        if (event.getOrganizerId() != null) {
            dto.setOrganizerId(event.getOrganizerId().getId());
            dto.setOrganizerName(event.getOrganizerId().getFullName());
        }
        
        if (event.getCategoryCollection() != null && !event.getCategoryCollection().isEmpty()) {
            Category firstCat = event.getCategoryCollection().iterator().next();
            if (firstCat != null) {
                dto.setCategoryId(firstCat.getId());
                dto.setCategoryName(firstCat.getName());
            }
        }
        
        return dto;
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
}
