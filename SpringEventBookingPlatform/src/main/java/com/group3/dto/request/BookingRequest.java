package com.group3.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Sự kiện không được trống")
    private Integer eventId;

    @Min(value = 1, message = "Số lượng vé không hợp lệ (>0)")
    private int quantity;

    @Size(max = 50, message = "Phương thức thanh toán không hợp lệ")
    private String paymentMethod;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
