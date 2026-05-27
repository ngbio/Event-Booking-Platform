package com.group3.repository.impl;

import com.group3.pojo.StatusTicket;
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

    private static final int TICKET_VALID = 1;
    private static final int TICKET_CHECKED_IN = 2;

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
    public TicketDetail getTicketByQrCode(String qrCode) {
        if (qrCode == null || qrCode.isBlank()) {
            return null;
        }

        String hql = "SELECT DISTINCT t FROM TicketDetail t "
                + "LEFT JOIN FETCH t.bookingId b "
                + "LEFT JOIN FETCH b.eventId e "
                + "LEFT JOIN FETCH e.organizerId "
                + "LEFT JOIN FETCH b.userId "
                + "LEFT JOIN FETCH t.statusId "
                + "WHERE t.qrCode = :qrCode";

        try {
            return getCurrentSession().createQuery(hql, TicketDetail.class)
                    .setParameter("qrCode", qrCode.trim())
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
                + "LEFT JOIN FETCH b.userId "
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
            predicates.add(b.equal(root.get("bookingId").get("userId").get("id"), userId));
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        q.orderBy(b.desc(root.get("id")));

        Query<TicketDetail> query = session.createQuery(q);
        applyPagination(query, params);
        return query.getResultList();
    }

    @Override
    public TicketDetail updateTicket(TicketDetail ticket) {
        ticket.setUpdatedDate(new Date());
        return (TicketDetail) getCurrentSession().merge(ticket);
    }

    @Override
    public boolean checkIn(String qrCode, Integer organizerId) {
        if (organizerId == null) {
            return false;
        }

        TicketDetail ticket = getTicketByQrCode(qrCode);
        if (ticket == null
                || ticket.getStatusId() == null
                || ticket.getStatusId().getId() != TICKET_VALID
                || ticket.getBookingId() == null
                || ticket.getBookingId().getEventId() == null
                || ticket.getBookingId().getEventId().getOrganizerId() == null
                || !organizerId.equals(ticket.getBookingId().getEventId().getOrganizerId().getId())) {
            return false;
        }

        StatusTicket checkedInStatus = getCurrentSession().get(StatusTicket.class, TICKET_CHECKED_IN);
        ticket.setStatusId(checkedInStatus);
        ticket.setUpdatedDate(new Date());
        getCurrentSession().merge(ticket);
        return true;
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
