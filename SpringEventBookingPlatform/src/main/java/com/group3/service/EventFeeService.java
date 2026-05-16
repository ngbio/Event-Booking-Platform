/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.group3.service;

import com.group3.dto.response.EventFeeResponse;
import com.group3.pojo.EventFee;
import java.util.List;

/**
 *
 * @author thanh
 */
public interface EventFeeService {
    EventFeeResponse createEventFee(EventFee eventFee);
    EventFeeResponse getEventFeeById(Integer id);
    List<EventFeeResponse> getEventFeeByEventId(Integer eventId);
}
