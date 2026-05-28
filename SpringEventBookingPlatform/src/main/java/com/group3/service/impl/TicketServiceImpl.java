package com.group3.service.impl;

import com.group3.dto.response.TicketResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.TicketDetail;
import com.group3.pojo.User;
import com.group3.repository.TicketDetailRepository;
import com.group3.service.TicketService;
import com.group3.utils.DTOMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final int ROLE_ATTENDEE = 3;
    private static final int USER_ACTIVE = 2;

    @Autowired
    private TicketDetailRepository ticketDetailRepo;

    @Override
    public List<TicketResponse> getMyTickets(User attendee, Map<String, String> params) {
        validateAttendee(attendee);
        return DTOMapper.toTicketResponseList(this.ticketDetailRepo.getTicketsByUser(attendee.getId(), params));
    }

    //lây chi tiết vé, 
    // chỉ được xem nếu là chủ sở hữu và vé phải hợp lệ (valid hoặc checked-in)
    @Override
    public TicketResponse getTicketDetail(Integer ticketId, User attendee) {
        validateAttendee(attendee);

        TicketDetail ticket = this.ticketDetailRepo.getTicketById(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Khong tim thay ve");
        }
        if (!isTicketOwner(ticket, attendee)) {
            throw new UnauthorizedException("Ban khong co quyen xem ve nay");
        }

        return DTOMapper.toTicketResponse(ticket);
    }

    private void validateAttendee(User attendee) {
        if (attendee == null || attendee.getRoleId() == null || attendee.getStatusId() == null) {
            throw new UnauthorizedException("Nguoi dung khong hop le");
        }
        if (attendee.getRoleId().getId() != ROLE_ATTENDEE || attendee.getStatusId().getId() != USER_ACTIVE) {
            throw new UnauthorizedException("Chi attendee ACTIVE moi duoc xem ve");
        }
    }

    private boolean isTicketOwner(TicketDetail ticket, User attendee) {
        return ticket.getBookingId() != null
                && ticket.getBookingId().getUserId() != null
                && ticket.getBookingId().getUserId().getId().equals(attendee.getId());
    }
}
