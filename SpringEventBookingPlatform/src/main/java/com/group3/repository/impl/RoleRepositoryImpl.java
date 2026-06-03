



package com.group3.repository.impl;



import com.group3.pojo.Role;
import com.group3.repository.RoleRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;


@Repository
@Transactional
public class RoleRepositoryImpl implements RoleRepository {
    
    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public List<Role> findAll() {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Role> q = session.createNamedQuery("Role.findAll", Role.class);
        return q.list();
    }

    @Override
    public Role findById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Role> q = session.createNamedQuery("Role.findById", Role.class);
        q.setParameter("id", id);
        return q.uniqueResult();
    }
}
