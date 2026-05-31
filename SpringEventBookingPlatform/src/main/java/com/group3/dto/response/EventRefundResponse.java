/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.dto.response;

import java.math.BigDecimal;

/**
 *
 * @author thanh
 */
public class EventRefundResponse {

    private Integer id;
    private String title;
    private String organizerName;
    private Integer soldTickets;
    private BigDecimal price;

    public EventRefundResponse(Integer id, String title, String organizerName, Integer soldTickets, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.organizerName = organizerName;
        this.soldTickets = soldTickets;
        this.price = price;
    }

    public EventRefundResponse() {
    }

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

    public Integer getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(Integer soldTickets) {
        this.soldTickets = soldTickets;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
