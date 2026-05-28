package com.group3.service;

import com.group3.dto.response.EventFinancialStatsResponse;
import com.group3.dto.response.OrganizerStatsOverviewResponse;
import java.security.Principal;

public interface StatsService {
    OrganizerStatsOverviewResponse getOrganizerOverview(Principal principal);
    EventFinancialStatsResponse getEventFinancialStats(Principal principal, Integer eventId);
}
