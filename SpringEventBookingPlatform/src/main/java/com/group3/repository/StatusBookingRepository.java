/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository;

import com.group3.pojo.StatusBooking;

/**
 *
 * @author thanh
 */
public interface StatusBookingRepository {

    StatusBooking getStatusBookingById(Integer id);

    boolean changeStatusBooking(Integer bookingId, Integer statusId);
}
