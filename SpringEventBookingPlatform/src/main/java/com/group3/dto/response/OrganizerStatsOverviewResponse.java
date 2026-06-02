package com.group3.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class OrganizerStatsOverviewResponse {
    private Integer organizerId;
    private String organizerName;
    private Long totalEvents;
    private Long totalPaidBookings;
    private Long totalTicketsSold;
    private BigDecimal totalRevenue;
    private List<RevenuePeriodResponse> monthlyRevenue;
    private List<RevenuePeriodResponse> quarterlyRevenue;
    private List<RevenuePeriodResponse> yearlyRevenue;

    public OrganizerStatsOverviewResponse() {
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getTotalPaidBookings() {
        return totalPaidBookings;
    }

    public void setTotalPaidBookings(Long totalPaidBookings) {
        this.totalPaidBookings = totalPaidBookings;
    }

    public Long getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public void setTotalTicketsSold(Long totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<RevenuePeriodResponse> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<RevenuePeriodResponse> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<RevenuePeriodResponse> getQuarterlyRevenue() {
        return quarterlyRevenue;
    }

    public void setQuarterlyRevenue(List<RevenuePeriodResponse> quarterlyRevenue) {
        this.quarterlyRevenue = quarterlyRevenue;
    }

    public List<RevenuePeriodResponse> getYearlyRevenue() {
        return yearlyRevenue;
    }

    public void setYearlyRevenue(List<RevenuePeriodResponse> yearlyRevenue) {
        this.yearlyRevenue = yearlyRevenue;
    }
}
