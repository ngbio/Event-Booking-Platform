package com.group3.service.impl;

import com.group3.pojo.Event;
import com.group3.pojo.StatusEvent;
import com.group3.repository.EventRepository;
import com.group3.repository.StatusEventRepository;
import com.group3.service.StatusEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusEventServiceImpl implements StatusEventService {

    @Autowired
    private StatusEventRepository statusEventRepo;
    @Autowired
    private EventRepository eventRepo;

    @Override
    public StatusEvent getStatusEventById(Integer id) {
        return this.statusEventRepo.getStatusEventById(id);
    }

    @Override
    public boolean changeStatusEvent(Integer eventId, Integer statusId) {
        if (eventId == null || statusId == null) {
            return false;
        }
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            return false;
        }
        StatusEvent newStatus = this.statusEventRepo.getStatusEventById(statusId);
        if (newStatus == null) {
            return false;
        }
        try {
            event.setStatusId(newStatus);
            this.eventRepo.updateEvent(event);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
