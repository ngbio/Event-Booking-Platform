
package com.group3.service;

import com.group3.dto.response.EventFeeResponse;
import com.group3.pojo.EventFee;
import java.util.List;

public interface EventFeeService {
    EventFeeResponse createEventFee(EventFee eventFee);
    EventFeeResponse getEventFeeById(Integer id);
    List<EventFeeResponse> getEventFeeByEventId(Integer eventId);
}
