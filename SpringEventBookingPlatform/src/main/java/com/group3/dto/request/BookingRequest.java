package com.group3.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull(message = "Vui long chon su kien")
    private Integer eventId;

    @Min(value = 1, message = "So luong ve phai lon hon 0")
    private int quantity;

    @Size(max = 50, message = "Phuong thuc thanh toan khong duoc qua 50 ky tu")
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
