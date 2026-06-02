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

    private static final int STATUS_BOOKING_PAID = 2;
    private static final int STATUS_EVENT_PUBLISHED = 2;
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Object[] getOrganizerOverview(Integer organizerId) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(DISTINCT e.id), "
                + "COUNT(DISTINCT b.id), "
                + "COALESCE(SUM(b.quantity), 0), "
                + "COALESCE(SUM(b.totalPrice), 0) "
                + "FROM Event e "
                + "LEFT JOIN Booking b ON b.eventId.id = e.id AND b.statusId.id = :paidStatus "
                + "WHERE e.organizerId.user.id = :organizerId";
        Query<Object[]> q = session.createQuery(hql, Object[].class);
        q.setParameter("paidStatus", STATUS_BOOKING_PAID);
        q.setParameter("organizerId", organizerId);
        return q.getSingleResult();
    }

    @Override
    public Object[] getEventFinancialStats(Integer eventId) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(DISTINCT b.id),"
                + "COALESCE(SUM(b.quantity), 0),"
                + "COALESCE(SUM(b.totalPrice),0) "
                + "FROM Booking b "
                + "WHERE b.eventId.id = :eventId AND b.statusId.id = :paidStatus";
        Query<Object[]> q = session.createQuery(hql, Object[].class);
        q.setParameter("eventId", eventId);
        q.setParameter("paidStatus", STATUS_BOOKING_PAID);
        return q.getSingleResult();
    }

    @Override
    public List<Object[]> getOrganizerRevenueByMonth(Integer organizerId, int year) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT MONTH(b.createdDate), COALESCE(SUM(b.totalPrice), 0) "
                + "FROM Booking b "
                + "WHERE YEAR(b.createdDate) = :year "
                + "AND b.statusId.id = :paidStatus "
                + "AND b.eventId.organizerId.user.id = :organizerId "
                + "GROUP BY MONTH(b.createdDate) "
                + "ORDER BY MONTH(b.createdDate)";

        return session.createQuery(hql, Object[].class)
                .setParameter("year", year)
                .setParameter("paidStatus", STATUS_BOOKING_PAID)
                .setParameter("organizerId", organizerId)
                .getResultList();
    }

    @Override
    public List<Object[]> getOrganizerRevenueByYear(Integer organizerId) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT YEAR(b.createdDate), COALESCE(SUM(b.totalPrice), 0) "
                + "FROM Booking b "
                + "WHERE b.statusId.id = :paidStatus "
                + "AND b.eventId.organizerId.user.id = :organizerId "
                + "GROUP BY YEAR(b.createdDate) "
                + "ORDER BY YEAR(b.createdDate)";

        return session.createQuery(hql, Object[].class)
                .setParameter("paidStatus", STATUS_BOOKING_PAID)
                .setParameter("organizerId", organizerId)
                .getResultList();
    }

    @Override
    public BigDecimal getTotalRevenue() {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT SUM(b.totalPrice) "
                + "FROM Booking b "
                + "WHERE b.statusId.id = 2";
        Query<BigDecimal> q = session.createQuery(hql, BigDecimal.class);
        BigDecimal sum = q.getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalFees() {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT SUM(e.listingFee) "
                + "FROM Event e "
                + "WHERE e.isSettlement = true";
        Query<BigDecimal> q = session.createQuery(hql, BigDecimal.class);
        BigDecimal sum = q.getSingleResult();
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public Long getTotalTicketsSold() {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT SUM(b.quantity) "
                + "FROM Booking b "
                + "WHERE b.statusId.id = :id";
        Query<Long> q = session.createQuery(hql, Long.class);
        q.setParameter("id", STATUS_BOOKING_PAID);
        Long count = q.getSingleResult();
        return count != null ? count : 0L;
    }

    @Override
    public Long getActiveEventsCount() {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(e.id) "
                + "FROM Event e "
                + "WHERE e.statusId.id = :id";
        Query<Long> q = session.createQuery(hql, Long.class);
        q.setParameter("id", STATUS_EVENT_PUBLISHED);
        return q.getSingleResult();
    }

    @Override
    public List<Object[]> getRevenueByMonth(int year
    ) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT MONTH(b.createdDate), SUM(b.totalPrice) FROM Booking b "
                + "WHERE YEAR(b.createdDate) = :year AND b.statusId.id = :id "
                + "GROUP BY MONTH(b.createdDate)";
        Query<Object[]> q = session.createQuery(hql, Object[].class);
        q.setParameter("year", year);
        q.setParameter("id", STATUS_BOOKING_PAID);
        return q.getResultList();
    }

    @Override
    public List<Object[]> getTicketsByCategory() {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT c.name, SUM(b.quantity) FROM Booking b "
                + "JOIN b.eventId e "
                + "JOIN e.categoryCollection c "
                + "WHERE b.statusId.id = :id "
                + "GROUP BY c.name";
        Query<Object[]> q = session.createQuery(hql, Object[].class);
        q.setParameter("id", STATUS_BOOKING_PAID);
        return q.getResultList();
    }
}
