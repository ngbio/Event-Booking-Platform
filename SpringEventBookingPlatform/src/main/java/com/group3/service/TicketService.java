package com.group3.service;

import java.util.List;
import java.util.Map;

import com.group3.dto.response.TicketResponse;
import com.group3.pojo.User;

public interface TicketService {
    List<TicketResponse> getMyTickets(User attendee, Map<String, String> params);
    TicketResponse getTicketDetail(Integer ticketId, User attendee);
    
}
