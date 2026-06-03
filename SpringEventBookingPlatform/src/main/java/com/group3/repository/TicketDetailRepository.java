package com.group3.repository;

import com.group3.pojo.TicketDetail;
import java.util.List;
import java.util.Map;

public interface TicketDetailRepository {
    TicketDetail addTicket(TicketDetail ticket);
    TicketDetail getTicketById(Integer id);
    List<TicketDetail> getTicketsByBooking(Integer bookingId);
    List<TicketDetail> getTicketsByUser(Integer userId, Map<String, String> params);
}
