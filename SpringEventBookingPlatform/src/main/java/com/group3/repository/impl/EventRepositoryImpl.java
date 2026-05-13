/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository.impl;

import com.group3.pojo.Event;
import com.group3.repository.EventRepository;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

/**
 *
 * @author THUAN
 */
@Repository
@Transactional
public class EventRepositoryImpl implements EventRepository {
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Event> getEvents() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Event> q = session.createNamedQuery("Event.findAll", Event.class);
        return q.list();
    }

    @Override
    public Event getEventById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Event> q = session.createNamedQuery("Event.findById", Event.class);
        q.setParameter("id", id);
        return q.uniqueResult();
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
}
