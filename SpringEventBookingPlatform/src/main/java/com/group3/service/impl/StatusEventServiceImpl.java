package com.group3.service.impl;

import com.group3.pojo.StatusEvent;
import com.group3.repository.StatusEventRepository;
import com.group3.service.StatusEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusEventServiceImpl implements StatusEventService{
    @Autowired
    private StatusEventRepository statusEventRepo;
    
    @Override
    public StatusEvent getStatusEventById(Integer id){
        return this.statusEventRepo.getStatusEventById(id);
    }
    
    @Override
    public boolean changeStatusEvent(Integer eventId, Integer statusId){
        if (eventId == null||statusId == null) return false;
        return this.statusEventRepo.changeStatusEvent(eventId, statusId);
    }
}
