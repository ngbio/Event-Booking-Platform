/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.service;

import com.group3.pojo.StatusEvent;

/**
 *
 * @author thanh
 */
public interface StatusEventService {

    StatusEvent getStatusEventById(Integer id);

    boolean changeStatusEvent(Integer eventId, Integer statusId);
}
