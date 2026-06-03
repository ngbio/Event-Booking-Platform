package com.group3.repository.impl;

import com.group3.pojo.TicketDetail;
import com.group3.repository.TicketDetailRepository;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class TicketDetailRepositoryImpl implements TicketDetailRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public TicketDetail addTicket(TicketDetail ticket) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(ticket);
        return ticket;
    }

    @Override
    public TicketDetail getTicketById(Integer id) {
        if (id == null) {
            return null;
        }

        String hql = "SELECT DISTINCT t FROM TicketDetail t "
                + "LEFT JOIN FETCH t.bookingId b "
                + "LEFT JOIN FETCH b.eventId e "
                + "LEFT JOIN FETCH e.organizerId o "
                + "LEFT JOIN FETCH o.user "
                + "LEFT JOIN FETCH b.attendeeId a "
                + "LEFT JOIN FETCH a.user "
                + "LEFT JOIN FETCH t.statusId "
                + "WHERE t.id = :id";

        try {
            return getCurrentSession().createQuery(hql, TicketDetail.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<TicketDetail> getTicketsByBooking(Integer bookingId) {
        if (bookingId == null) {
            return List.of();
        }

        String hql = "SELECT DISTINCT t FROM TicketDetail t "
                + "LEFT JOIN FETCH t.bookingId b "
                + "LEFT JOIN FETCH b.eventId "
                + "LEFT JOIN FETCH b.attendeeId a "
                + "LEFT JOIN FETCH a.user "
                + "LEFT JOIN FETCH t.statusId "
                + "WHERE b.id = :bookingId "
                + "ORDER BY t.id DESC";

        return getCurrentSession().createQuery(hql, TicketDetail.class)
                .setParameter("bookingId", bookingId)
                .getResultList();
    }

    @Override
    public List<TicketDetail> getTicketsByUser(Integer userId, Map<String, String> params) {
        Session session = getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<TicketDetail> q = b.createQuery(TicketDetail.class);
        Root<TicketDetail> root = q.from(TicketDetail.class);
        q.select(root).distinct(true);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (userId != null) {
            predicates.add(b.equal(root.get("bookingId").get("attendeeId").get("user").get("id"), userId));
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        q.orderBy(b.desc(root.get("id")));

        Query<TicketDetail> query = session.createQuery(q);
        applyPagination(query, params);
        return query.getResultList();
    }

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<TicketDetail> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();
        if (params == null) {
            return predicates;
        }

        String statusId = params.get("statusId");
        String eventId = params.get("eventId");
        String bookingId = params.get("bookingId");
        Date fromDate = parseDate(params.get("fromDate"), false);
        Date toDate = parseDate(params.get("toDate"), true);

        if (statusId != null && !statusId.isBlank()) {
            try {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            } catch (NumberFormatException ignored) {
            }
        }
        if (eventId != null && !eventId.isBlank()) {
            try {
                predicates.add(b.equal(root.get("bookingId").get("eventId").get("id"), Integer.parseInt(eventId)));
            } catch (NumberFormatException ignored) {
            }
        }
        if (bookingId != null && !bookingId.isBlank()) {
            try {
                predicates.add(b.equal(root.get("bookingId").get("id"), Integer.parseInt(bookingId)));
            } catch (NumberFormatException ignored) {
            }
        }
        if (fromDate != null) {
            predicates.add(b.greaterThanOrEqualTo(root.get("createdDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(b.lessThanOrEqualTo(root.get("createdDate"), toDate));
        }

        return predicates;
    }

    private void applyPagination(Query<TicketDetail> query, Map<String, String> params) {
        int defaultPageSize = getDefaultPageSize();
        int pageSize = defaultPageSize;
        int page = 1;

        if (params != null) {
            try {
                page = Integer.parseInt(params.getOrDefault("page", "1"));
            } catch (NumberFormatException ignored) {
            }
            try {
                pageSize = Integer.parseInt(params.getOrDefault("size", String.valueOf(defaultPageSize)));
            } catch (NumberFormatException ignored) {
            }
        }
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = defaultPageSize;
        }

        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
    }

    private int getDefaultPageSize() {
        String size = this.env.getProperty("ticket.page_size");
        if (size == null || size.isBlank()) {
            size = this.env.getProperty("booking.page_size");
        }
        if (size == null || size.isBlank()) {
            size = this.env.getProperty("user.page_size", "10");
        }
        return Integer.parseInt(size);
    }

    private Date parseDate(String value, boolean endOfDay) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String pattern = value.trim().length() == 10 ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm:ss";
        try {
            Date date = new SimpleDateFormat(pattern).parse(value.trim());
            if (endOfDay && pattern.equals("yyyy-MM-dd")) {
                return new Date(date.getTime() + 86399999);
            }
            return date;
        } catch (ParseException ex) {
            return null;
        }
    }

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }
}
