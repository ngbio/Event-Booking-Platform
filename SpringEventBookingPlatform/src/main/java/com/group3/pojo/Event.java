/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author thanh
 */
@Entity
@Table(name = "event")
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findById", query = "SELECT e FROM Event e WHERE e.id = :id"),
    @NamedQuery(name = "Event.findByTitle", query = "SELECT e FROM Event e WHERE e.title = :title"),
    @NamedQuery(name = "Event.findByImageUrl", query = "SELECT e FROM Event e WHERE e.imageUrl = :imageUrl"),
    @NamedQuery(name = "Event.findByVideoUrl", query = "SELECT e FROM Event e WHERE e.videoUrl = :videoUrl"),
    @NamedQuery(name = "Event.findByStartTime", query = "SELECT e FROM Event e WHERE e.startTime = :startTime"),
    @NamedQuery(name = "Event.findByEndTime", query = "SELECT e FROM Event e WHERE e.endTime = :endTime"),
    @NamedQuery(name = "Event.findByLocation", query = "SELECT e FROM Event e WHERE e.location = :location"),
    @NamedQuery(name = "Event.findByTotalTickets", query = "SELECT e FROM Event e WHERE e.totalTickets = :totalTickets"),
    @NamedQuery(name = "Event.findByPrice", query = "SELECT e FROM Event e WHERE e.price = :price"),
    @NamedQuery(name = "Event.findBySoldTickets", query = "SELECT e FROM Event e WHERE e.soldTickets = :soldTickets"),
    @NamedQuery(name = "Event.findByListingFee", query = "SELECT e FROM Event e WHERE e.listingFee = :listingFee"),
    @NamedQuery(name = "Event.findByIsPaidFee", query = "SELECT e FROM Event e WHERE e.isPaidFee = :isPaidFee"),
    @NamedQuery(name = "Event.findBySettlementCode", query = "SELECT e FROM Event e WHERE e.settlementCode = :settlementCode"),
    @NamedQuery(name = "Event.findByCreatedDate", query = "SELECT e FROM Event e WHERE e.createdDate = :createdDate"),
    @NamedQuery(name = "Event.findByUpdatedDate", query = "SELECT e FROM Event e WHERE e.updatedDate = :updatedDate")})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "title")
    private String title;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;
    @Size(max = 255)
    @Column(name = "video_url")
    private String videoUrl;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "location")
    private String location;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_tickets")
    private int totalTickets;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sold_tickets")
    private int soldTickets;
    @Column(name = "listing_fee")
    private BigDecimal listingFee;
    @Column(name = "is_paid_fee")
    private Boolean isPaidFee;
    @Size(max = 100)
    @Column(name = "settlement_code")
    private String settlementCode;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
    @JoinTable(name = "event_category", joinColumns = {
        @JoinColumn(name = "event_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "category_id", referencedColumnName = "id")})
    @ManyToMany
    private Collection<Category> categoryCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Collection<Booking> bookingCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Collection<EventFee> eventFeeCollection;
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private StatusEvent statusId;
    @JoinColumn(name = "organizer_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false)
    private Organizer organizerId;

    public Event() {
    }

    public Event(Integer id) {
        this.id = id;
    }

    public Event(Integer id, String title, Date startTime, Date endTime, String location, int totalTickets, int soldTickets) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.totalTickets = totalTickets;
        this.soldTickets = soldTickets;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public BigDecimal getListingFee() {
        return listingFee;
    }

    public void setListingFee(BigDecimal listingFee) {
        this.listingFee = listingFee;
    }

    public Boolean getIsPaidFee() {
        return isPaidFee;
    }

    public void setIsPaidFee(Boolean isPaidFee) {
        this.isPaidFee = isPaidFee;
    }

    public String getSettlementCode() {
        return settlementCode;
    }

    public void setSettlementCode(String settlementCode) {
        this.settlementCode = settlementCode;
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

    public Collection<Category> getCategoryCollection() {
        return categoryCollection;
    }

    public void setCategoryCollection(Collection<Category> categoryCollection) {
        this.categoryCollection = categoryCollection;
    }

    public Collection<Booking> getBookingCollection() {
        return bookingCollection;
    }

    public void setBookingCollection(Collection<Booking> bookingCollection) {
        this.bookingCollection = bookingCollection;
    }

    public Collection<EventFee> getEventFeeCollection() {
        return eventFeeCollection;
    }

    public void setEventFeeCollection(Collection<EventFee> eventFeeCollection) {
        this.eventFeeCollection = eventFeeCollection;
    }

    public StatusEvent getStatusId() {
        return statusId;
    }

    public void setStatusId(StatusEvent statusId) {
        this.statusId = statusId;
    }

    public Organizer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Organizer organizerId) {
        this.organizerId = organizerId;
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
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.group3.pojo.Event[ id=" + id + " ]";
    }
    
}
