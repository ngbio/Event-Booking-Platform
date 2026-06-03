
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
import java.util.Date;


@Entity
@Table(name = "ticket_detail")
@NamedQueries({
    @NamedQuery(name = "TicketDetail.findAll", query = "SELECT t FROM TicketDetail t"),
    @NamedQuery(name = "TicketDetail.findById", query = "SELECT t FROM TicketDetail t WHERE t.id = :id"),
    @NamedQuery(name = "TicketDetail.findByQrCode", query = "SELECT t FROM TicketDetail t WHERE t.qrCode = :qrCode"),
    @NamedQuery(name = "TicketDetail.findByCreatedDate", query = "SELECT t FROM TicketDetail t WHERE t.createdDate = :createdDate"),
    @NamedQuery(name = "TicketDetail.findByUpdatedDate", query = "SELECT t FROM TicketDetail t WHERE t.updatedDate = :updatedDate")})
public class TicketDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "qrCode")
    private String qrCode;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Booking bookingId;
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private StatusTicket statusId;

    public TicketDetail() {
    }

    public TicketDetail(Integer id) {
        this.id = id;
    }

    public TicketDetail(Integer id, String qrCode) {
        this.id = id;
        this.qrCode = qrCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Booking getBookingId() {
        return bookingId;
    }

    public void setBookingId(Booking bookingId) {
        this.bookingId = bookingId;
    }

    public StatusTicket getStatusId() {
        return statusId;
    }

    public void setStatusId(StatusTicket statusId) {
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
        
        if (!(object instanceof TicketDetail)) {
            return false;
        }
        TicketDetail other = (TicketDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.group3.pojo.TicketDetail[ id=" + id + " ]";
    }
    
}
