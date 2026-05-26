package com.group3.service;

import com.group3.pojo.StatusEvent;

public interface StatusEventService {

    StatusEvent getStatusEventById(Integer id);

    boolean changeStatusEvent(Integer eventId, Integer statusId);
}
