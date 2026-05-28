package com.group3.repository;

public interface StatsRepository {
    Object[] getOrganizerOverview(Integer organizerId);
    Object[] getEventFinancialStats(Integer eventId);
}
