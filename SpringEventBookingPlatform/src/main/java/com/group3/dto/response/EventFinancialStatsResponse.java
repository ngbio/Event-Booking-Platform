package com.group3.dto.response;

import java.math.BigDecimal;

public class EventFinancialStatsResponse {
    private Integer eventId;
    private String eventTitle;
    private Integer totalTickets;
    private Integer soldTickets;
    private Integer availableTickets;
    private Long paidBookings;
    private Long ticketsSold;
    private BigDecimal ticketPrice;
    private BigDecimal grossRevenue;
    private BigDecimal listingFee;
    private BigDecimal netRevenue;

    public EventFinancialStatsResponse() {
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Integer soldTickets) {
        this.soldTickets = soldTickets;
    }

    public Integer getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(Integer availableTickets) {
        this.availableTickets = availableTickets;
    }

    public Long getPaidBookings() {
        return paidBookings;
    }

    public void setPaidBookings(Long paidBookings) {
        this.paidBookings = paidBookings;
    }

    public Long getTicketsSold() {
        return ticketsSold;
    }

    public void setTicketsSold(Long ticketsSold) {
        this.ticketsSold = ticketsSold;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public BigDecimal getGrossRevenue() {
        return grossRevenue;
    }

    public void setGrossRevenue(BigDecimal grossRevenue) {
        this.grossRevenue = grossRevenue;
    }

    public BigDecimal getListingFee() {
        return listingFee;
    }

    public void setListingFee(BigDecimal listingFee) {
        this.listingFee = listingFee;
    }

    public BigDecimal getNetRevenue() {
        return netRevenue;
    }

    public void setNetRevenue(BigDecimal netRevenue) {
        this.netRevenue = netRevenue;
    }
}
