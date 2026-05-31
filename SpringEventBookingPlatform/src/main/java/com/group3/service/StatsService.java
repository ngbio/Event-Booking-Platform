package com.group3.service;

import com.group3.dto.response.EventFinancialStatsResponse;
import com.group3.dto.response.OrganizerStatsOverviewResponse;
import java.security.Principal;
import java.util.Map;

public interface StatsService {

    OrganizerStatsOverviewResponse getOrganizerOverview(Principal principal);

    EventFinancialStatsResponse getEventFinancialStats(Principal principal, Integer eventId);

    Map<String, Object> getDashboardStats();
}
