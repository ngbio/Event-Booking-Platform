
package com.group3.repository;

import com.group3.pojo.StatusEvent;


public interface StatusEventRepository {

    StatusEvent getStatusEventById(Integer id);

    boolean changeStatusEvent(Integer eventId, Integer statusId);
}
