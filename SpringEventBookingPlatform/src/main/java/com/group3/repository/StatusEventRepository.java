
package com.group3.repository;

import com.group3.pojo.Event;
import com.group3.pojo.StatusEvent;


public interface StatusEventRepository {
    StatusEvent findById(Integer id);

    StatusEvent getStatusEventById(Integer id);

    void changeStatusEvent(Event event);
}
