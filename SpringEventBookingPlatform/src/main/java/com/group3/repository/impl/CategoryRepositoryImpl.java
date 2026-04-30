package com.group3.repository.impl;

import com.group3.pojo.Category;
import com.group3.repository.CategoryRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CategoryRepositoryImpl implements CategoryRepository{
    @Autowired
    private LocalSessionFactoryBean fatory;
    @Override
    public List<Category> getCates() {
        Session session = fatory.getObject().getCurrentSession();
        Query query = session.createQuery("FROM Category",Category.class);
        return query.getResultList();
    }
    
}
