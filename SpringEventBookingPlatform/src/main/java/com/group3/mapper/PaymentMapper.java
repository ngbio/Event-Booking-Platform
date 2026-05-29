package com.group3.mapper;

import com.group3.pojo.Payment;
import com.group3.dto.response.PaymentResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentMapper {

    /**
     * Convert Payment entity to ResPaymentDTO
     */
    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setCreatedDate(payment.getCreatedDate());
        response.setUpdatedDate(payment.getUpdatedDate());
        
        if (payment.getBookingId() != null) {
            response.setBookingId(payment.getBookingId().getId());
            if (payment.getBookingId().getAttendeeId() != null
                    && payment.getBookingId().getAttendeeId().getUser() != null) {
                response.setUserId(payment.getBookingId().getAttendeeId().getUser().getId());
                response.setUsername(payment.getBookingId().getAttendeeId().getUser().getEmail());
            }
        }
        
        if (payment.getStatusId() != null) {
            response.setStatusId(payment.getStatusId().getId());
            // Note: Statuspay entity should have getName() method
        }
        
        return response;
    }

    /**
     * Convert List of Payments to List of ResPaymentDTOs
     */
    public static List<PaymentResponse> toResponseList(List<Payment> payments) {
        if (payments == null) {
            return new ArrayList<>();
        }
        return payments.stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
