package com.group3.service.impl;

import com.group3.dto.response.EventFinancialStatsResponse;
import com.group3.dto.response.OrganizerStatsOverviewResponse;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.Event;
import com.group3.pojo.User;
import com.group3.repository.EventRepository;
import com.group3.repository.StatsRepository;
import com.group3.repository.UserRepository;
import com.group3.service.StatsService;
import java.math.BigDecimal;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private static final int ROLE_ORGANIZER = 2;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private StatsRepository statsRepo;

    @Override
    @Transactional(readOnly = true)
    public OrganizerStatsOverviewResponse getOrganizerOverview(Principal principal) {
        User organizer = validateAndGetOrganizer(principal);
        Object[] stats = this.statsRepo.getOrganizerOverview(organizer.getId());

        OrganizerStatsOverviewResponse response = new OrganizerStatsOverviewResponse();
        response.setOrganizerId(organizer.getId());
        response.setOrganizerName(organizer.getFullName());
        response.setTotalEvents(toLong(stats[0]));
        response.setTotalPaidBookings(toLong(stats[1]));
        response.setTotalTicketsSold(toLong(stats[2]));
        response.setTotalRevenue(toBigDecimal(stats[3]));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFinancialStatsResponse getEventFinancialStats(Principal principal, Integer eventId) {
        User organizer = validateAndGetOrganizer(principal);
        Event event = validateEventOwnership(eventId, organizer);
        Object[] stats = this.statsRepo.getEventFinancialStats(event.getId());

        BigDecimal grossRevenue = toBigDecimal(stats[2]);
        BigDecimal listingFee = event.getListingFee() != null ? event.getListingFee() : BigDecimal.ZERO;

        EventFinancialStatsResponse response = new EventFinancialStatsResponse();
        response.setEventId(event.getId());
        response.setEventTitle(event.getTitle());
        response.setTotalTickets(event.getTotalTickets());
        response.setSoldTickets(event.getSoldTickets());
        response.setAvailableTickets(Math.max(event.getTotalTickets() - event.getSoldTickets(), 0));
        response.setPaidBookings(toLong(stats[0]));
        response.setTicketsSold(toLong(stats[1]));
        response.setTicketPrice(event.getPrice() != null ? event.getPrice() : BigDecimal.ZERO);
        response.setGrossRevenue(grossRevenue);
        response.setListingFee(listingFee);
        response.setNetRevenue(grossRevenue.subtract(listingFee));
        return response;
    }

    private User validateAndGetOrganizer(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }

        User user = this.userRepo.findUserByEmail(principal.getName());
        if (user == null || user.getRoleId() == null || user.getRoleId().getId() != ROLE_ORGANIZER) {
            throw new UnauthorizedException("Chỉ nhà tổ chức mới xem được thống kê");
        }
        return user;
    }

    private Event validateEventOwnership(Integer eventId, User organizer) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }
        if (event.getOrganizerId() == null || !organizer.getId().equals(event.getOrganizerId().getUserId())) {
            throw new UnauthorizedException("Bạn không có quyền xem thống kê sự kiện này");
        }
        return event;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return BigDecimal.valueOf(((Number) value).doubleValue());
    }
}
