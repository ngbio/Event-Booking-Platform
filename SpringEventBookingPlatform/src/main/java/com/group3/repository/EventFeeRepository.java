/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.EventFee;
import java.util.List;

/**
 *
 * @author thanh
 */
public interface EventFeeRepository {
    EventFee addEventFee(EventFee fee);
    EventFee getEventFeeById(Integer id);
    List<EventFee> getEventFeeByEventId(Integer eventId);
}
