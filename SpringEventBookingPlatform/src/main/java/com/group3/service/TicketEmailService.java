package com.group3.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TicketEmailService {
    void sendTicketsEmail(String toEmail, String customerName, Integer bookingId,
            String eventTitle, String eventLocation, Date eventStartTime, Date eventEndTime,
            BigDecimal totalPrice, List<String> qrCodes);

    void sendPaymentReminderEmail(String toEmail, String customerName, Integer bookingId,
            String eventTitle, String eventLocation, Date eventStartTime, Date paymentDeadline,
            BigDecimal totalPrice);
}
