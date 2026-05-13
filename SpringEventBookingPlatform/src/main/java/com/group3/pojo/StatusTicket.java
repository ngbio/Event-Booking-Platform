/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author thanh
 */
@Entity
@Table(name = "status_ticket")
@NamedQueries({
    @NamedQuery(name = "StatusTicket.findAll", query = "SELECT s FROM StatusTicket s"),
    @NamedQuery(name = "StatusTicket.findById", query = "SELECT s FROM StatusTicket s WHERE s.id = :id"),
    @NamedQuery(name = "StatusTicket.findByName", query = "SELECT s FROM StatusTicket s WHERE s.name = :name")})
public class StatusTicket implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusId")
    private Collection<TicketDetail> ticketDetailCollection;

    public StatusTicket() {
    }

    public StatusTicket(Integer id) {
        this.id = id;
    }

    public StatusTicket(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<TicketDetail> getTicketDetailCollection() {
        return ticketDetailCollection;
    }

    public void setTicketDetailCollection(Collection<TicketDetail> ticketDetailCollection) {
        this.ticketDetailCollection = ticketDetailCollection;
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
        if (!(object instanceof StatusTicket)) {
            return false;
        }
        StatusTicket other = (StatusTicket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.group3.pojo.StatusTicket[ id=" + id + " ]";
    }
    
}
