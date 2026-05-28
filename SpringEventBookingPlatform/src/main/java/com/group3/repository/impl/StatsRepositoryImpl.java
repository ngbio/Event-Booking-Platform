package com.group3.repository.impl;

import com.group3.repository.StatsRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class StatsRepositoryImpl implements StatsRepository {

    private static final int BOOKING_PAID = 2;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Object[] getOrganizerOverview(Integer organizerId) {
        String hql = "SELECT COUNT(DISTINCT e.id), "
                + "COUNT(DISTINCT b.id), "
                + "COALESCE(SUM(b.quantity), 0), "
                + "COALESCE(SUM(b.totalPrice), 0) "
                + "FROM Event e "
                + "LEFT JOIN Booking b ON b.eventId.id = e.id AND b.statusId.id = :paidStatus "
                + "WHERE e.organizerId.id = :organizerId";

        return getCurrentSession().createQuery(hql, Object[].class)
                .setParameter("paidStatus", BOOKING_PAID)
                .setParameter("organizerId", organizerId)
                .getSingleResult();
    }

    @Override
    public Object[] getEventFinancialStats(Integer eventId) {
        String hql = "SELECT COUNT(DISTINCT b.id), "
                + "COALESCE(SUM(b.quantity), 0), "
                + "COALESCE(SUM(b.totalPrice), 0) "
                + "FROM Booking b "
                + "WHERE b.eventId.id = :eventId "
                + "AND b.statusId.id = :paidStatus";

        return getCurrentSession().createQuery(hql, Object[].class)
                .setParameter("eventId", eventId)
                .setParameter("paidStatus", BOOKING_PAID)
                .getSingleResult();
    }

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }
}
