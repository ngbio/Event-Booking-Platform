/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service.impl;

import com.group3.dto.response.EventFeeResponse;
import com.group3.pojo.EventFee;
import com.group3.repository.EventFeeRepository;
import com.group3.service.EventFeeService;
import com.group3.utils.DTOMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author thanh
 */
@Service
public class EventFeeServiceImpl implements EventFeeService{
    @Autowired
    private EventFeeRepository eventFeeRepo;
    
    @Override
    public EventFeeResponse createEventFee(EventFee fee){
        EventFee savedFee=this.eventFeeRepo.addEventFee(fee);
        return DTOMapper.toEventFeeResponse(savedFee);
    }
    
    @Override
    public EventFeeResponse getEventFeeById(Integer id){
        EventFee eventFee = this.eventFeeRepo.getEventFeeById(id);
        return DTOMapper.toEventFeeResponse(eventFee);
    }
    
    @Override
    public List<EventFeeResponse> getEventFeeByEventId(Integer eventId){
        List<EventFee> fees = this.eventFeeRepo.getEventFeeByEventId(eventId);
        return DTOMapper.toEventFeeResponseList(fees);
    }
}
