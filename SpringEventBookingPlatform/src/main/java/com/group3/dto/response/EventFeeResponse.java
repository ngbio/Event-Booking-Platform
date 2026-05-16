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
public class EventFeeResponse {
    private Integer id;
    private BigDecimal amount;
    private String paymentMethod;
    private String transcationId;
    private Date createdDate;
    
    private Integer eventId;
    private String eventTitle;
    
    private Integer statusId;
    private String statusName;
    
    public EventFeeResponse(){};

    public EventFeeResponse(Integer id, BigDecimal amount, String paymentMethod, String transcationId, Date createdDate, Integer eventId, String eventTitle, Integer statusId, String statusName) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transcationId = transcationId;
        this.createdDate = createdDate;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.statusId = statusId;
        this.statusName = statusName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTranscationId() {
        return transcationId;
    }

    public void setTranscationId(String transcationId) {
        this.transcationId = transcationId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    
}
