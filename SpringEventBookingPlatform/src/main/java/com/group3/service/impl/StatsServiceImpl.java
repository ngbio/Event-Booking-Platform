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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 1. Thống kê thẻ tổng quan
        stats.put("totalRevenue", statsRepo.getTotalRevenue());
        stats.put("totalFees", statsRepo.getTotalFees());
        stats.put("totalTicketsSold", statsRepo.getTotalTicketsSold());
        stats.put("activeEventsCount", statsRepo.getActiveEventsCount());

        // 2. Xử lý biểu đồ doanh thu năm nay (Khởi tạo mảng 12 tháng bằng 0)
        int currentYear = LocalDate.now().getYear();
        List<Object[]> monthlyData = statsRepo.getRevenueByMonth(currentYear);
        List<BigDecimal> revenueMonthList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            revenueMonthList.add(BigDecimal.ZERO);
        }

        // Đổ data từ DB vào đúng vị trí tháng (tháng 1 index là 0)
        for (Object[] row : monthlyData) {
            int month = (Integer) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            revenueMonthList.set(month - 1, revenue);
        }
        stats.put("revenueMonthList", revenueMonthList);

        // 3. Xử lý biểu đồ danh mục
        List<Object[]> categoryData = statsRepo.getTicketsByCategory();
        List<String> categoryNames = new ArrayList<>();
        List<Long> categoryCounts = new ArrayList<>();

        for (Object[] row : categoryData) {
            categoryNames.add((String) row[0]); // Tên danh mục
            categoryCounts.add((Long) row[1]);  // Số vé
        }
        stats.put("categoryNames", categoryNames);
        stats.put("categoryCounts", categoryCounts);

        return stats;
    }
}
