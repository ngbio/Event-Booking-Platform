/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.mapper;

import com.group3.dto.response.EventFeeResponse;
import com.group3.pojo.EventFee;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author thanh
 */
public class EventFeeMapper {

    public static EventFeeResponse toResponse(EventFee fee) {
        if (fee == null) {
            return null;
        }
        EventFeeResponse response = new EventFeeResponse();
        response.setId(fee.getId());
        response.setAmount(fee.getAmount());
        response.setPaymentMethod(fee.getPaymentMethod());
        response.setTranscationId(fee.getTransactionId());
        response.setCreatedDate(fee.getCreatedDate());

        if (fee.getEventId() != null) {
            response.setEventId(fee.getEventId().getId());
            response.setEventTitle(fee.getEventId().getTitle());
        }

        if (fee.getStatusId() != null) {
            response.setStatusId(fee.getStatusId().getId());
            response.setStatusName(fee.getStatusId().getName());
        }

        return response;
    }

    public static List<EventFeeResponse> toResponseList(List<EventFee> fees) {
        if (fees == null || fees.isEmpty()) {
            return new ArrayList<>();
        }
        return fees.stream()
                .map(EventFeeMapper::toResponse)
                .collect(Collectors.toList());
    }
    
}
