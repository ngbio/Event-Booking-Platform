
package com.group3.repository.impl;

import com.group3.pojo.StatusUser;
import com.group3.pojo.User;
import com.group3.repository.StatusUserRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class StatusUserRepositoryImpl implements StatusUserRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    
    @Override
    public StatusUser getStatusUserById(Integer id){
        Session session = this.factory.getObject().getCurrentSession();
        return session.get(StatusUser.class, id);
    }
    
    @Override
    public boolean changeStatusUser(Integer userId, Integer statusId){
        try {
            Session session = this.factory.getObject().getCurrentSession();
            if (userId == null || statusId == null) {
                return false;
            }
            User user = session.get(User.class, userId);
            if (user != null) {
                StatusUser newStatus = session.get(StatusUser.class, statusId);
                user.setStatusId(newStatus);
                session.merge(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
