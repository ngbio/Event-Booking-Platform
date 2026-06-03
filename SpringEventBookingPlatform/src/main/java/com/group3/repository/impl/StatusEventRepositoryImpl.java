
package com.group3.repository.impl;

import com.group3.pojo.Event;
import com.group3.pojo.StatusEvent;
import com.group3.repository.StatusEventRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class StatusEventRepositoryImpl implements StatusEventRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public StatusEvent getStatusEventById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(StatusEvent.class, id);
    }

    @Override
    public boolean changeStatusEvent(Integer eventId, Integer statusId) {
        Session session = this.factory.getObject().getCurrentSession();
        try {
            if (eventId == null || statusId == null) {
                return false;
            }
            Event event = session.get(Event.class, eventId);
            if (event != null) {
                StatusEvent newStatus = session.get(StatusEvent.class, statusId);
                event.setStatusId(newStatus);
                session.merge(event);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
