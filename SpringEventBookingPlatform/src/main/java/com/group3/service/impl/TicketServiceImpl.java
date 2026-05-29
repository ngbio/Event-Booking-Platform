package com.group3.service.impl;

import com.group3.dto.response.TicketResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.TicketDetail;
import com.group3.pojo.User;
import com.group3.repository.TicketDetailRepository;
import com.group3.service.TicketService;
import com.group3.service.UserService;
import com.group3.utils.DTOMapper;
import java.security.Principal;
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
    
    @Autowired
    private UserService userService;
    
    private User validateAndGetCurrentUser(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        User user = userService.getUserEntityByEmail(principal.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return user;
    }
    
    
    private void validateAttendee(User attendee) {
        if (attendee == null || attendee.getRoleId() == null || attendee.getStatusId() == null) {
            throw new UnauthorizedException("Người dùng không hợp lệ");
        }
        if (attendee.getRoleId().getId() != ROLE_ATTENDEE || attendee.getStatusId().getId() != USER_ACTIVE) {
            throw new UnauthorizedException("Chỉ tài khoản active của attendee mới được xem vé");
        }
    }

    private boolean isTicketOwner(TicketDetail ticket, User attendee) {
        return ticket.getBookingId() != null
                && ticket.getBookingId().getAttendeeId() != null
                && ticket.getBookingId().getAttendeeId().getUser() != null
                && ticket.getBookingId().getAttendeeId().getUser().getId().equals(attendee.getId());
    }

    @Override
    public List<TicketResponse> getMyTickets(Principal principal, Map<String, String> params) {
        User attendee = validateAndGetCurrentUser(principal);
        validateAttendee(attendee);
        return DTOMapper.toTicketResponseList(this.ticketDetailRepo.getTicketsByUser(attendee.getId(), params));
    }

    //lây chi tiết vé, 
    // chỉ được xem nếu là chủ sở hữu và vé phải hợp lệ (valid hoặc checked-in)
    @Override
    public TicketResponse getTicketDetail(Integer ticketId, Principal principal) {
        User attendee = validateAndGetCurrentUser(principal);
        validateAttendee(attendee);

        TicketDetail ticket = this.ticketDetailRepo.getTicketById(ticketId);
        if (ticket == null) {
            throw new ResourceNotFoundException("Không tìm thấy vé");
        }
        if (!isTicketOwner(ticket, attendee)) {
            throw new UnauthorizedException("Bạn không có quyền xem vé này");
        }

        return DTOMapper.toTicketResponse(ticket);
    }
}
