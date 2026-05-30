/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository.impl;

import com.group3.pojo.Booking;
import com.group3.pojo.StatusBooking;
import com.group3.repository.StatusBookingRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thanh
 */
@Repository
public class StatusBookingRepositoryImpl implements StatusBookingRepository{
    @Autowired
    private LocalSessionFactoryBean fatory;
    
    @Override
    public StatusBooking getStatusBookingById(Integer id) {
        Session session = this.fatory.getObject().getCurrentSession();
        return session.get(StatusBooking.class, id);
    }

    @Override
    public boolean changeStatusBooking(Integer bookingId, Integer statusId) {
        Session session = this.fatory.getObject().getCurrentSession();
        try {
            if (bookingId == null || statusId == null) {
                return false;
            }
            Booking booking = session.get(Booking.class, bookingId);
            if (booking != null) {
                StatusBooking newStatus = session.get(StatusBooking.class, statusId);
                booking.setStatusId(newStatus);
                session.merge(booking);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    
    
}
