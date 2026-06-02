package com.group3.repository;

import java.math.BigDecimal;
import java.util.List;

public interface StatsRepository {

    Object[] getOrganizerOverview(Integer organizerId);

    Object[] getEventFinancialStats(Integer eventId);

    List<Object[]> getOrganizerRevenueByMonth(Integer organizerId, int year);

    List<Object[]> getOrganizerRevenueByYear(Integer organizerId);

    BigDecimal getTotalRevenue();

    BigDecimal getTotalFees();

    Long getTotalTicketsSold();

    Long getActiveEventsCount();

    List<Object[]> getRevenueByMonth(int year);

    List<Object[]> getTicketsByCategory();
}
