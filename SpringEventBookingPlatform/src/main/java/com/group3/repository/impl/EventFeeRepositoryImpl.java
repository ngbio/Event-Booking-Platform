/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.repository.impl;

import com.group3.pojo.EventFee;
import com.group3.repository.EventFeeRepository;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thanh
 */
@Repository
@Transactional
public class EventFeeRepositoryImpl implements EventFeeRepository {

    @Autowired
    LocalSessionFactoryBean factory;

    @Override
    public EventFee addEventFee(EventFee fee) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(fee);
        return fee;
    }

    @Override
    public EventFee getEventFeeById(Integer id) {
        if (id == null) {
            return null;
        }
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(EventFee.class, id);
    }

    @Override
    public List<EventFee> getEventFeeByEventId(Integer eventId) {
        if (eventId==null) return new ArrayList<>();
        Session session = this.factory.getObject().getCurrentSession();
        Query<EventFee> q = session.createQuery("FROM EventFee fee WHERE fee.eventId.id = : eventId", EventFee.class);
        q.setParameter("eventId", eventId);
        return q.getResultList();
    }
}
