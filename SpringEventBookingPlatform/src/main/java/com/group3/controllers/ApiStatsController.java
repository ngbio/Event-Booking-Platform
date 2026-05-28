package com.group3.controllers;

import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.EventFinancialStatsResponse;
import com.group3.dto.response.OrganizerStatsOverviewResponse;
import com.group3.service.StatsService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/secure/organizer/stats")
@CrossOrigin
public class ApiStatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<OrganizerStatsOverviewResponse>> getOrganizerOverview(Principal principal) {
        OrganizerStatsOverviewResponse stats = this.statsService.getOrganizerOverview(principal);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay thong ke tong quan thanh cong", stats));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<ApiResponse<EventFinancialStatsResponse>> getEventFinancialStats(
            Principal principal,
            @PathVariable("id") Integer id) {
        EventFinancialStatsResponse stats = this.statsService.getEventFinancialStats(principal, id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lay bao cao tai chinh su kien thanh cong", stats));
    }
}
