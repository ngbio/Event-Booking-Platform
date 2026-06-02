package com.group3.repository.impl;

import com.group3.pojo.Booking;
import com.group3.repository.BookingRepository;
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
public class BookingRepositoryImpl implements BookingRepository {

    private static final int PAID_BOOKING_STATUS = 2;

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public Booking addBooking(Booking booking) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Integer id) {
        if (id == null) {
            return null;
        }

        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT DISTINCT b FROM Booking b "
                + "LEFT JOIN FETCH b.eventId "
                + "LEFT JOIN FETCH b.attendeeId a "
                + "LEFT JOIN FETCH a.user "
                + "LEFT JOIN FETCH b.statusId "
                + "WHERE b.id = :id";

        try {
            Query<Booking> q = session.createQuery(hql, Booking.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Booking> q = b.createQuery(Booking.class);
        Root<Booking> root = q.from(Booking.class);
        q.select(root).distinct(true);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (userId != null) {
            predicates.add(b.equal(root.get("attendeeId").get("user").get("id"), userId));
        }
        
        applyWhereAndOrder(q, b, root, predicates);

        Query<Booking> query = session.createQuery(q);
        applyPagination(query, params);
        return query.getResultList();
    }

    @Override
    public List<Booking> getBookingsByEventId(Integer eventId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Booking> q = b.createQuery(Booking.class);
        Root<Booking> root = q.from(Booking.class);
        q.select(root).distinct(true);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (eventId != null) {
            predicates.add(b.equal(root.get("eventId").get("id"), eventId));
        }

        applyWhereAndOrder(q, b, root, predicates);

        Query<Booking> query = session.createQuery(q);
        applyPagination(query, params);
        return query.getResultList();
    }

    @Override
    public List<Booking> getBookingsByOrganizer(Integer organizerId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Booking> q = b.createQuery(Booking.class);
        Root<Booking> root = q.from(Booking.class);
        q.select(root).distinct(true);

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (organizerId != null) {
            predicates.add(b.equal(root.get("eventId").get("organizerId").get("user").get("id"), organizerId));
        }

        applyWhereAndOrder(q, b, root, predicates);

        Query<Booking> query = session.createQuery(q);
        applyPagination(query, params);
        return query.getResultList();
    }

    @Override
    public Booking updateBooking(Booking booking) {
        Session session = this.factory.getObject().getCurrentSession();
        return (Booking) session.merge(booking);
    }

    @Override
    public long countBookingsByUserId(Integer userId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Booking> root = q.from(Booking.class);
        q.select(b.countDistinct(root));

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (userId != null) {
            predicates.add(b.equal(root.get("attendeeId").get("user").get("id"), userId));
        }
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return session.createQuery(q).getSingleResult();
    }

    @Override
    public long countBookingsByEventId(Integer eventId, Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Booking> root = q.from(Booking.class);
        q.select(b.countDistinct(root));

        List<Predicate> predicates = buildPredicates(b, root, params);
        if (eventId != null) {
            predicates.add(b.equal(root.get("eventId").get("id"), eventId));
        }
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return session.createQuery(q).getSingleResult();
    }

    @Override
    public boolean existsPaidBooking(Integer eventId, Integer userId) {
        if (eventId == null || userId == null) {
            return false;
        }

        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT COUNT(b.id) FROM Booking b "
                + "WHERE b.eventId.id = :eventId "
                + "AND b.attendeeId.user.id = :userId "
                + "AND b.statusId.id = :statusId";

        Long count = session.createQuery(hql, Long.class)
                .setParameter("eventId", eventId)
                .setParameter("userId", userId)
                .setParameter("statusId", PAID_BOOKING_STATUS)
                .getSingleResult();

        return count > 0;
    }

    private List<Predicate> buildPredicates(CriteriaBuilder b, Root<Booking> root, Map<String, String> params) {
        List<Predicate> predicates = new ArrayList<>();

        if (params == null) {
            return predicates;
        }

        String kw = params.get("kw"); 
        String statusId = params.get("statusId");
        String eventId = params.get("eventId");
        Date fromDate = parseDate(params.get("fromDate"), false);
        Date toDate = parseDate(params.get("toDate"), true);
        if (kw != null && !kw.trim().isEmpty()) {
            String keyword = "%" + kw.trim().toLowerCase() + "%";
            Predicate matchEmail = b.like(b.lower(root.get("attendeeId").get("user").get("email")), keyword);
            predicates.add(b.or(matchEmail));
        }
        if (statusId != null && !statusId.isBlank()) {
            if (statusId.contains(",")) {
                String[] ids = statusId.split(",");
                List<Integer> listIds = new ArrayList<>();
                for (String id : ids) {
                    listIds.add(Integer.parseInt(id.trim()));
                }
                predicates.add(root.get("statusId").get("id").in(listIds));
            } else {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            }
        }
        if (eventId != null && !eventId.isBlank()) {
            predicates.add(b.equal(root.get("eventId").get("id"), Integer.parseInt(eventId)));
        }
        if (fromDate != null) {
            predicates.add(b.greaterThanOrEqualTo(root.get("createdDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(b.lessThanOrEqualTo(root.get("createdDate"), toDate));
        }

        return predicates;
    }

    private void applyWhereAndOrder(CriteriaQuery<Booking> q, CriteriaBuilder b, Root<Booking> root, List<Predicate> predicates) {
        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        q.orderBy(b.asc(root.get("createdDate")));
    }

    private void applyPagination(Query<Booking> query, Map<String, String> params) {
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
        String size = this.env.getProperty("booking.page_size");
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

    
    @Override
    public int updateStatusByEventId(Integer eventId, Integer oldStatusId, Integer newStatusId) {
        if (eventId == null || oldStatusId == null || newStatusId == null) {
            return 0;
        }
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "UPDATE Booking b SET b.statusId.id = :newStatusId " +
                     "WHERE b.eventId.id = :eventId AND b.statusId.id = :oldStatusId";

        org.hibernate.query.MutationQuery query = session.createMutationQuery(hql);
        
        query.setParameter("eventId", eventId);
        query.setParameter("oldStatusId", oldStatusId);
        query.setParameter("newStatusId", newStatusId);

        return query.executeUpdate(); 
    }

}
