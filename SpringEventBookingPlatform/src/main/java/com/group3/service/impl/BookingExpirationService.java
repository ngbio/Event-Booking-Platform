package com.group3.service.impl;

import com.group3.pojo.Booking;
import com.group3.pojo.Payment;
import com.group3.pojo.StatusBooking;
import com.group3.pojo.StatusPay;
import java.util.Date;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@PropertySource("classpath:configs.properties")
public class BookingExpirationService {

    private static final int BOOKING_PENDING = 1;
    private static final int BOOKING_CANCELLED = 5;
    private static final int PAYMENT_PENDING = 1;
    private static final int PAYMENT_FAILED = 3;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    @Scheduled(fixedDelayString = "${booking.expire_check_delay_ms:60000}")
    public void cancelExpiredPendingBookings() {
        Date expiredBefore = new Date(System.currentTimeMillis() - getPaymentTimeoutMillis());
        Session session = getCurrentSession();

        List<Booking> expiredBookings = session.createQuery(
                "SELECT b FROM Booking b WHERE b.statusId.id = :pendingStatus AND b.createdDate <= :expiredBefore",
                Booking.class)
                .setParameter("pendingStatus", BOOKING_PENDING)
                .setParameter("expiredBefore", expiredBefore)
                .getResultList();

        if (expiredBookings.isEmpty()) {
            return;
        }

        StatusBooking cancelledStatus = session.get(StatusBooking.class, BOOKING_CANCELLED);
        StatusPay failedStatus = session.get(StatusPay.class, PAYMENT_FAILED);
        Date now = new Date();

        for (Booking booking : expiredBookings) {
            booking.setStatusId(cancelledStatus);
            booking.setUpdatedDate(now);
            session.merge(booking);

            session.createMutationQuery("UPDATE Payment p SET p.statusId = :failedStatus, p.updatedDate = :now "
                    + "WHERE p.bookingId.id = :bookingId AND p.statusId.id = :pendingStatus")
                    .setParameter("failedStatus", failedStatus)
                    .setParameter("now", now)
                    .setParameter("bookingId", booking.getId())
                    .setParameter("pendingStatus", PAYMENT_PENDING)
                    .executeUpdate();
        }
    }

    private long getPaymentTimeoutMillis() {
        long minutes = Long.parseLong(env.getProperty("booking.payment_timeout_minutes", "5"));
        return minutes * 60_000L;
    }

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }
}
