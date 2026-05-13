/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.Event;
import java.util.List;
import java.util.Map;

/**
 *
 * @author THUAN
 */
public interface EventRepository {
    List<Event> getEvents();
    Event getEventById(Integer id);
    Event addEvent(Event event);
    Event updateEvent(Event event);
    boolean deleteEvent(Integer id);
    List<Event> findByCategory(Integer categoryId);
    List<Event> findByParams(Map<String, String> params);
}
