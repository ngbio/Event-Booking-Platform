package com.group3.dto.request;

import jakarta.validation.constraints.NotNull;

public class MomoCreatePaymentRequest {

    @NotNull
    private Integer bookingId;

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
}
