package com.group3.service;

import java.util.List;
import java.util.Map;

import com.group3.dto.response.TicketResponse;
import java.security.Principal;

public interface TicketService {
    List<TicketResponse> getMyTickets(Principal principal, Map<String, String> params);
    TicketResponse getTicketDetail(Integer ticketId, Principal principal);
    
}
