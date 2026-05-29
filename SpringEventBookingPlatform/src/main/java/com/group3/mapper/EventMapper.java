package com.group3.mapper;

import com.group3.dto.request.EventRequest;
import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.dto.response.EventResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

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
        response.setPrice(event.getPrice());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setCreatedDate(event.getCreatedDate());
        response.setUpdatedDate(event.getUpdatedDate());
        response.setIsPaidFee(event.getIsPaidFee());
        response.setListingFee(event.getListingFee());
        response.setSettlementCode(event.getSettlementCode());

        Integer evtTotal = event.getTotalTickets();
        response.setTotalTickets(evtTotal);
        int total = (evtTotal != null) ? evtTotal : 0;

        Integer evtSold = event.getSoldTickets();
        response.setSoldTickets(evtSold);
        int sold = (evtSold != null) ? evtSold : 0;
        
        response.setAvailableTickets(Math.max(total - sold, 0));
        
        if (event.getStatusId() != null) {
            response.setStatusId(event.getStatusId().getId());
            response.setStatusName(event.getStatusId().getName());
        }
        if (event.getOrganizerId() != null) {
            response.setOrganizerId(event.getOrganizerId().getUserId());
            if (event.getOrganizerId().getUser() != null) {
                response.setOrganizerName(event.getOrganizerId().getUser().getFullName());
            } else {
                response.setOrganizerName(event.getOrganizerId().getOrganizationName());
            }
        }

        if (event.getCategoryCollection() != null && !event.getCategoryCollection().isEmpty()) {
            // Fix an toàn thêm cho Category
            List<Category> catList = new ArrayList<>(event.getCategoryCollection());
            Category firstCat = catList.get(0);
            if (firstCat != null) {
                response.setCategoryId(firstCat.getId());
                response.setCategoryName(firstCat.getName());
            }
        }

        return response;
    }
    
    public static List<EventResponse> toResponseList(List<Event> events) {
        if (events == null) {
            return new ArrayList<>();
        }
        return events.stream()
            .map(EventMapper::toResponse)
            .collect(Collectors.toList());
    }

    public static Event toEntity(EventRequest request) {
        if (request == null) {
            return null;
        }
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

    public static Event toEntity(EventRequest request, Event existingEvent) {
        if (request == null || existingEvent == null) {
            return existingEvent;
        }

        String title = request.getTitle();
        if (title != null) {
            existingEvent.setTitle(title);
        }
        
        String desc = request.getDescription();
        if (desc != null) {
            existingEvent.setDescription(desc);
        }
        
        Date startTime = request.getStartTime();
        if (startTime != null) {
            existingEvent.setStartTime(startTime);
        }
        
        Date endTime = request.getEndTime();
        if (endTime != null) {
            existingEvent.setEndTime(endTime);
        }
        
        String location = request.getLocation();
        if (location != null) {
            existingEvent.setLocation(location);
        }
        
        Integer totalTickets = request.getTotalTickets();
        if (totalTickets != null) {
            existingEvent.setTotalTickets(totalTickets);
        }
        
        BigDecimal price = request.getPrice();
        if (price != null) {
            existingEvent.setPrice(price);
        }
   
        existingEvent.setUpdatedDate(new Date());

        return existingEvent;
    }
}
