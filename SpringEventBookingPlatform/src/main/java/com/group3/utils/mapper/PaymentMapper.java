package com.group3.utils.mapper;

import com.group3.pojo.Payment;
import com.group3.pojo.response.ResPaymentDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentMapper {

    /**
     * Convert Payment entity to ResPaymentDTO
     */
    public static ResPaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        ResPaymentDTO dto = new ResPaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setActive(payment.getActive());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        
        if (payment.getBookingId() != null) {
            dto.setBookingId(payment.getBookingId().getId());
        }
        
        if (payment.getUserId() != null) {
            dto.setUserId(payment.getUserId().getId());
            dto.setUsername(payment.getUserId().getUsername());
        }
        
        if (payment.getStatusId() != null) {
            dto.setStatusId(payment.getStatusId().getId());
            // Note: Statuspay entity should have getName() method
        }
        
        return dto;
    }

    /**
     * Convert List of Payments to List of ResPaymentDTOs
     */
    public static List<ResPaymentDTO> toDTOList(List<Payment> payments) {
        if (payments == null) {
            return new ArrayList<>();
        }
        return payments.stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
