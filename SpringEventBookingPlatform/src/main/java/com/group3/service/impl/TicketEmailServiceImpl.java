package com.group3.service.impl;

import com.group3.service.TicketEmailService;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:configs.properties")
public class TicketEmailServiceImpl implements TicketEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Override
    @Async
    public void sendTicketsEmail(String toEmail, String customerName, Integer bookingId,
            String eventTitle, String eventLocation, Date eventStartTime, Date eventEndTime,
            BigDecimal totalPrice, List<String> qrCodes) {
        if (!isMailEnabled() || toEmail == null || toEmail.isBlank()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(getFromAddress());
            helper.setSubject("Vé của bạn cho sự kiện " + safeText(eventTitle));
            helper.setText(buildTicketEmail(customerName, bookingId, eventTitle, eventLocation,
                    eventStartTime, eventEndTime, totalPrice, qrCodes), true);
            mailSender.send(message);
        } catch (Exception ex) {
            System.err.println("Không thể gửi email về cho booking #" + bookingId + ": " + ex.getMessage());
        }
    }

    private boolean isMailEnabled() {
        return Boolean.parseBoolean(env.getProperty("mail.enabled", "false"))
                && !env.getProperty("mail.username", "").isBlank()
                && !env.getProperty("mail.password", "").isBlank();
    }

    private String getFromAddress() {
        String from = env.getProperty("mail.from", "");
        return from.isBlank() ? env.getProperty("mail.username", "") : from;
    }

    private String buildTicketEmail(String customerName, Integer bookingId,
            String eventTitle, String eventLocation, Date eventStartTime, Date eventEndTime,
            BigDecimal totalPrice, List<String> qrCodes) {
        StringBuilder ticketsHtml = new StringBuilder();
        if (qrCodes != null) {
            for (int i = 0; i < qrCodes.size(); i++) {
                ticketsHtml.append("<tr>")
                        .append("<td style='padding:10px;border:1px solid #eee;'>Ve ").append(i + 1).append("</td>")
                        .append("<td style='padding:10px;border:1px solid #eee;font-family:monospace;'>")
                        .append(escape(qrCodes.get(i)))
                        .append("</td>")
                        .append("</tr>");
            }
        }

        return "<div style='font-family:Arial,sans-serif;color:#222;line-height:1.6;'>"
                + "<h2 style='color:#c3156b;'>Đặt vé thành công</h2>"
                + "<p>Xin chào " + escape(blankToDefault(customerName, "bạn")) + ",</p>"
                + "<p>Booking #" + bookingId + " của bạn đã tạo vé thành công.</p>"
                + "<div style='padding:14px;border:1px solid #eee;border-radius:8px;background:#fafafa;'>"
                + "<p><strong>Sự kiện:</strong> " + escape(safeText(eventTitle)) + "</p>"
                + "<p><strong>Địa điểm:</strong> " + escape(blankToDefault(eventLocation, "Đang cập nhật")) + "</p>"
                + "<p><strong>Bắt đầu:</strong> " + formatDate(eventStartTime) + "</p>"
                + "<p><strong>Kết thúc:</strong> " + formatDate(eventEndTime) + "</p>"
                + "<p><strong>Tổng tiền:</strong> " + formatCurrency(totalPrice) + "</p>"
                + "</div>"
                + "<h3>Mã vé</h3>"
                + "<table style='border-collapse:collapse;width:100%;'>" + ticketsHtml + "</table>"
                + "<p style='margin-top:18px;color:#666;'>Vui lòng đưa mã QR/mã vé này khi check-in sự kiện.</p>"
                + "</div>";
    }

    private String formatDate(Date value) {
        if (value == null) {
            return "Đang cập nhật";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(value);
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null || BigDecimal.ZERO.compareTo(value) == 0) {
            return "Free";
        }
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(value);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "sự kiện" : value;
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
