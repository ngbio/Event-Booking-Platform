/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.dto.response;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author thanh
 */
public class EventSettlementResponse {
    private Integer id;
    private String title;

    private String organizerName;
    private String email; 
    private String phone;

    private Date endTime; 

    private int soldTickets;
    private BigDecimal price;
    private BigDecimal totalRevenue; 
    private BigDecimal listingFee; 
    private BigDecimal actualPayout;

    private Boolean isSettlement;
    private String settlementCode;

    public EventSettlementResponse(Integer id, String title, String organizerName, String email, String phone, Date endTime, int soldTickets, BigDecimal price, BigDecimal totalRevenue, BigDecimal listingFee, BigDecimal actualPayout, Boolean isSettlement, String settlementCode) {
        this.id = id;
        this.title = title;
        this.organizerName = organizerName;
        this.email = email;
        this.phone = phone;
        this.endTime = endTime;
        this.soldTickets = soldTickets;
        this.price = price;
        this.totalRevenue = totalRevenue;
        this.listingFee = listingFee;
        this.actualPayout = actualPayout;
        this.isSettlement = isSettlement;
        this.settlementCode = settlementCode;
    }
    
    public EventSettlementResponse(){};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getListingFee() {
        return listingFee;
    }

    public void setListingFee(BigDecimal listingFee) {
        this.listingFee = listingFee;
    }

    public BigDecimal getActualPayout() {
        return actualPayout;
    }

    public void setActualPayout(BigDecimal actualPayout) {
        this.actualPayout = actualPayout;
    }

    public Boolean getIsSettlement() {
        return isSettlement;
    }

    public void setIsSettlement(Boolean isSettlement) {
        this.isSettlement = isSettlement;
    }

    public String getSettlementCode() {
        return settlementCode;
    }

    public void setSettlementCode(String settlementCode) {
        this.settlementCode = settlementCode;
    }
    
    
}
