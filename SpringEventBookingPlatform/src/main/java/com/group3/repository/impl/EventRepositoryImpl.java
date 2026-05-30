package com.group3.repository.impl;

import com.group3.pojo.Category;
import com.group3.pojo.Event;
import com.group3.pojo.StatusEvent;
import com.group3.repository.EventRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Repository
@Transactional
@PropertySource("classpath:configs.properties")
public class EventRepositoryImpl implements EventRepository {

    @Autowired
    private Environment env;
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Event> getEvents(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Event> q = b.createQuery(Event.class);
        Root<Event> root = q.from(Event.class);
        q.select(root).distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            String searchBy = params.get("searchBy");
            String statusId = params.get("statusId");
            String organizerId = params.get("organizerId");
            String categoryId = params.get("categoryId");
            String activeOnly = params.get("activeOnly");
            if (statusId != null && !statusId.isBlank()) {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            }
            if ("true".equalsIgnoreCase(activeOnly)) {
                predicates.add(b.greaterThanOrEqualTo(root.get("endTime"), new Date()));
            }
            if (organizerId != null && !organizerId.isBlank()) {
                predicates.add(b.equal(root.get("organizerId").get("userId"), Integer.parseInt(organizerId)));
            }
            if (categoryId != null && !categoryId.isBlank()) {
                Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.INNER);
                predicates.add(b.equal(categoryJoin.get("id"), Integer.parseInt(categoryId)));
            }
            if (kw != null && !kw.isBlank()) {
                String t = kw.trim();

                if (searchBy == null || searchBy.isEmpty()) {
                    Predicate byTitle = b.like(root.get("title"), "%" + t + "%");
                    Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.LEFT);
                    Predicate byCategoryName = b.like(categoryJoin.get("name"), "%" + t + "%");
                    predicates.add(b.or(byTitle, byCategoryName));
                } else if ("title".equals(searchBy)) {
                    predicates.add(b.like(root.get("title"), "%" + t + "%"));
                } else if ("category".equals(searchBy)) {
                    Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.INNER);
                    predicates.add(b.like(categoryJoin.get("name"), "%" + t + "%"));
                }
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }
        
        q.orderBy(
                b.asc(root.get("statusId").get("id")),//Xep su kien can duyet len truoc
                b.desc(root.get("id"))
        );

        Query query = session.createQuery(q);

        // phan trang
        int defaultPageSize = Integer.parseInt(this.env.getProperty("event.page_size"));
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

        return query.getResultList();
    }

    @Override
    public Event getEventById(Integer id) {
        if (id == null) {
            return null;
        }
        Session session = this.factory.getObject().getCurrentSession();

        String hql = "SELECT DISTINCT e FROM Event e "
                + "LEFT JOIN FETCH e.categoryCollection "
                + "LEFT JOIN FETCH e.organizerId o "
                + "LEFT JOIN FETCH o.user "
                + "WHERE e.id = :id";

        try {
            Query<Event> q = session.createQuery(hql, Event.class);
            q.setParameter("id", id);
            return q.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }
    }

    @Override
    public Event addEvent(Event event) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(event);
        return event;
    }

    @Override
    public Event updateEvent(Event event) {
        Session session = this.factory.getObject().getCurrentSession();
        session.merge(event);
        return event;
    }

    @Override
    public boolean deleteEvent(Integer id) {
        try {
            Session session = this.factory.getObject().getCurrentSession();
            Event event = getEventById(id);
            if (event != null) {
                session.remove(event);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Event> findByCategory(Integer categoryId) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT DISTINCT e FROM Event e JOIN e.categoryCollection c WHERE c.id = :categoryId";
        Query<Event> q = session.createQuery(hql, Event.class);
        q.setParameter("categoryId", categoryId);
        return q.list();
    }

    @Override
    public List<Event> findByParams(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        String hql = "SELECT e FROM Event e WHERE 1=1";

        if (params.containsKey("title") && !params.get("title").isEmpty()) {
            hql += " AND e.title LIKE :title";
        }
        if (params.containsKey("location") && !params.get("location").isEmpty()) {
            hql += " AND e.location LIKE :location";
        }
        if (params.containsKey("categoryId") && !params.get("categoryId").isEmpty()) {
            hql += " AND :categoryId IN (SELECT c.id FROM e.categoryCollection c)";
        }

        Query<Event> q = session.createQuery(hql, Event.class);

        if (params.containsKey("title") && !params.get("title").isEmpty()) {
            q.setParameter("title", "%" + params.get("title") + "%");
        }
        if (params.containsKey("location") && !params.get("location").isEmpty()) {
            q.setParameter("location", "%" + params.get("location") + "%");
        }
        if (params.containsKey("categoryId") && !params.get("categoryId").isEmpty()) {
            q.setParameter("categoryId", Integer.parseInt(params.get("categoryId")));
        }

        return q.list();
    }

    @Override
    public long countEvents(Map<String, String> params) {
        Session session = this.factory.getObject().getCurrentSession();
        CriteriaBuilder b = session.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<Event> root = q.from(Event.class);

        q.select(b.countDistinct(root));

        List<Predicate> predicates = new ArrayList<>();

        if (params != null) {
            String kw = params.get("kw");
            String searchBy = params.get("searchBy");
            String statusId = params.get("statusId");
            String organizerId = params.get("organizerId");
            String categoryId = params.get("categoryId");
            String activeOnly = params.get("activeOnly");
            if (statusId != null && !statusId.isBlank()) {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            }
            if ("true".equalsIgnoreCase(activeOnly)) {
                predicates.add(b.greaterThanOrEqualTo(root.get("endTime"), new java.util.Date()));
            }
            if (organizerId != null && !organizerId.isBlank()) {
                predicates.add(b.equal(root.get("organizerId").get("userId"), Integer.parseInt(organizerId)));
            }
            if (categoryId != null && !categoryId.isBlank()) {
                Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.INNER);
                predicates.add(b.equal(categoryJoin.get("id"), Integer.parseInt(categoryId)));
            }
            if (kw != null && !kw.isBlank()) {
                String t = kw.trim();

                if (searchBy == null || searchBy.isEmpty()) {
                    Predicate byTitle = b.like(root.get("title"), "%" + t + "%");
                    Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.LEFT);
                    Predicate byCategoryName = b.like(categoryJoin.get("name"), "%" + t + "%");
                    predicates.add(b.or(byTitle, byCategoryName));
                } else if ("title".equals(searchBy)) {
                    predicates.add(b.like(root.get("title"), "%" + t + "%"));
                } else if ("category".equals(searchBy)) {
                    Join<Event, Category> categoryJoin = root.join("categoryCollection", JoinType.INNER);
                    predicates.add(b.like(categoryJoin.get("name"), "%" + t + "%"));
                }
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        Query query = session.createQuery(q);
        return (long) query.getSingleResult();
    }
    
    @Override
    public List<Event>getEventsByIds(List<Integer> EventIds){
        Session session = this.factory.getObject().getCurrentSession();
        Query<Event> q = session.createQuery("FROM Event e WHERE e.id IN :ids",Event.class);
        q.setParameter("ids",EventIds);
        return q.getResultList();
    }

    @Override
    public int updateExpiredPublishedEvents(Integer publishedStatusId, Integer completedStatusId, Date now) {
        if (publishedStatusId == null || completedStatusId == null || now == null) {
            return 0;
        }

        Session session = this.factory.getObject().getCurrentSession();
        StatusEvent completedStatus = session.get(StatusEvent.class, completedStatusId);
        if (completedStatus == null) {
            return 0;
        }

        Query<?> q = session.createQuery(
                "UPDATE Event e "
                + "SET e.statusId = :completedStatus, e.updatedDate = :now "
                + "WHERE e.statusId.id = :publishedStatusId "
                + "AND e.endTime IS NOT NULL "
                + "AND e.endTime < :now");
        q.setParameter("completedStatus", completedStatus);
        q.setParameter("publishedStatusId", publishedStatusId);
        q.setParameter("now", now);
        return q.executeUpdate();
    }
}
