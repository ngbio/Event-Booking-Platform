package com.group3.service.impl;

import com.group3.dto.response.EventRefundResponse;
import com.group3.dto.response.EventResponse;
import com.group3.dto.response.EventSettlementResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.pojo.Event;
import com.group3.repository.EventRepository;
import com.group3.service.EventService;
import com.group3.utils.DTOMapper;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@PropertySource("classpath:configs.properties")
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepo;

    private static final int PUBLISHED = 2;
    private static final int COMPLETED = 4;

    private void refreshExpiredPublishedEvents() {
        this.eventRepo.updateExpiredPublishedEvents(PUBLISHED, COMPLETED, new Date());
    }

    private Event validateAndGetPublicEvent(Integer eventId) {
        Event event = eventRepo.getEventById(eventId);
        Integer statusId = event != null && event.getStatusId() != null ? event.getStatusId().getId() : null;
        if (event == null || PUBLISHED != statusId) {
            throw new ResourceNotFoundException("Khong tim thay su kien hoac su kien chua mo ban");
        }
        return event;
    }

    @Override
    @Transactional
    public List<EventResponse> getEvents(Map<String, String> params) {
        refreshExpiredPublishedEvents();
        List<Event> events = this.eventRepo.getEvents(params);
        return DTOMapper.toEventResponseList(events);
    }

    @Override
    @Transactional
    public EventResponse getEventById(Integer eventId) {
        refreshExpiredPublishedEvents();
        Event event = validateAndGetPublicEvent(eventId);
        return DTOMapper.toEventResponse(event);
    }

    @Override
    public boolean deleteEvent(Integer eventId) {
        return this.eventRepo.deleteEvent(eventId);
    }

    @Override
    public int getAvailableTickets(Integer eventId) {
        refreshExpiredPublishedEvents();
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return 0;
        }
        if (event.getStatusId() == null || PUBLISHED != event.getStatusId().getId()) {
            return 0;
        }
        return event.getTotalTickets() - event.getSoldTickets();
    }

    @Override
    public Long countEvents(Map<String, String> params) {
        return this.eventRepo.countEvents(params);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByIds(List<Integer> eventIds) {
        List<Event> events = this.eventRepo.getEventsByIds(eventIds);
        events.removeIf(event -> event.getStatusId() == null
                || event.getStatusId().getId() != PUBLISHED
                || event.getEndTime() == null
                || event.getEndTime().before(new Date()));
        return DTOMapper.toEventResponseList(events);
    }

    @Override
    public EventResponse getEventByIdForAdmin(Integer eventId) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }
        return DTOMapper.toEventResponse(event);
    }

    @Override
    public List<EventRefundResponse> getEventsForRefund() {
        return DTOMapper.toEventRefundResponseList(this.eventRepo.getEventsForRefund());
    }

    @Override
    public List<EventSettlementResponse> getEventsForSettlement() {
        return DTOMapper.toEventSettlementResponseList(this.eventRepo.getEventsForSettlement());
    }

    @Override
    @Transactional
    public boolean processSettlement(Integer eventId, String settlementCode) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null || event.getStatusId() == null || event.getStatusId().getId() != COMPLETED) {
            return false;
        }
        event.setIsSettlement(true);
        event.setSettlementCode(settlementCode);
        this.eventRepo.updateEvent(event);

        return true;
    }
}
