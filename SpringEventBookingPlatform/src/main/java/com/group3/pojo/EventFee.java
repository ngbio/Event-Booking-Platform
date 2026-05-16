/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author thanh
 */
@Entity
@Table(name = "event_fee")
@NamedQueries({
    @NamedQuery(name = "EventFee.findAll", query = "SELECT e FROM EventFee e"),
    @NamedQuery(name = "EventFee.findById", query = "SELECT e FROM EventFee e WHERE e.id = :id"),
    @NamedQuery(name = "EventFee.findByAmount", query = "SELECT e FROM EventFee e WHERE e.amount = :amount"),
    @NamedQuery(name = "EventFee.findByPaymentMethod", query = "SELECT e FROM EventFee e WHERE e.paymentMethod = :paymentMethod"),
    @NamedQuery(name = "EventFee.findByTransactionId", query = "SELECT e FROM EventFee e WHERE e.transactionId = :transactionId"),
    @NamedQuery(name = "EventFee.findByCreatedDate", query = "SELECT e FROM EventFee e WHERE e.createdDate = :createdDate")})
public class EventFee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;
    @Size(max = 50)
    @Column(name = "payment_method")
    private String paymentMethod;
    @Size(max = 255)
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Event eventId;
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private StatusPay statusId;

    public EventFee() {
    }

    public EventFee(Integer id) {
        this.id = id;
    }

    public EventFee(Integer id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Event getEventId() {
        return eventId;
    }

    public void setEventId(Event eventId) {
        this.eventId = eventId;
    }

    public StatusPay getStatusId() {
        return statusId;
    }

    public void setStatusId(StatusPay statusId) {
        this.statusId = statusId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventFee)) {
            return false;
        }
        EventFee other = (EventFee) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.group3.pojo.EventFee[ id=" + id + " ]";
    }
    
}
