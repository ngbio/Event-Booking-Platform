/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.Event;
import com.group3.pojo.StatusEvent;

/**
 *
 * @author thanh
 */
public interface StatusEventRepository {
    StatusEvent getById(Integer id);

    StatusEvent getStatusEventById(Integer id);

    void changeStatusEvent(Event event, StatusEvent newStatus);
}
