package com.group3.repository.impl;

import com.group3.repository.StatsRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
                + "WHERE e.organizerId.user.id = :organizerId";

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

    @Override
    public BigDecimal getTotalRevenue() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<BigDecimal> q = session.createQuery("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.statusId.id = 2", BigDecimal.class);
        BigDecimal sum = q.getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalFees() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<BigDecimal> q = session.createQuery("SELECT SUM(e.listingFee) FROM Event e WHERE e.isSettlement = true", BigDecimal.class);
        BigDecimal sum = q.getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public Long getTotalTicketsSold() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Long> q = session.createQuery("SELECT SUM(b.quantity) FROM Booking b WHERE b.statusId.id = 2", Long.class);
        Long count = q.getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public Long getActiveEventsCount() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Long> q = session.createQuery("SELECT COUNT(e.id) FROM Event e WHERE e.statusId.id = 2", Long.class);
        return q.getSingleResult();
    }

    @Override
    public List<Object[]> getRevenueByMonth(int year) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT MONTH(b.createdDate), SUM(b.totalPrice) FROM Booking b "
                + "WHERE YEAR(b.createdDate) = :year AND b.statusId.id = 2 "
                + "GROUP BY MONTH(b.createdDate)";
        Query<Object[]> q = session.createQuery(hql, Object[].class);
        q.setParameter("year", year);
        return q.getResultList();
    }

    @Override
    public List<Object[]> getTicketsByCategory() {
        Session session = this.factory.getObject().getCurrentSession();
        // Sửa: JOIN e.categoryCollection c để khớp chính xác với entity Event
        String hql = "SELECT c.name, SUM(b.quantity) FROM Booking b "
                + "JOIN b.eventId e "
                + "JOIN e.categoryCollection c "
                + "WHERE b.statusId.id = 2 "
                + "GROUP BY c.name";
        return session.createQuery(hql, Object[].class).getResultList();
    }
}
