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
    private LocalSessionFactoryBean factory;
    
    @Override
    public List<Category> getCategories() {
        Session session = this.factory.getObject().getCurrentSession();
        Query query = session.createQuery("FROM Category",Category.class);
        return query.getResultList();
    }
    
    @Override
    public Category getCateById(Integer id) {
        Session session = this.factory.getObject().getCurrentSession();
        Query<Category> q = session.createNamedQuery("Category.findById", Category.class);
        q.setParameter("id", id);
        return q.uniqueResult();
    }
    
    @Override
    public boolean deleteCategory(Integer id){
        try {
            Session session = this.factory.getObject().getCurrentSession();
            Category category = getCateById(id);
            if (category!=null){
                session.remove(category);
                return true;
            }
        }
        catch (Exception e){
            return false;
        }
        return false;
    }
    
    @Override
    public void addOrUpdateCategory(Category cate){
        Session session = this.factory.getObject().getCurrentSession();
        if (cate.getId()!=null){
            session.merge(cate);
        }
        else session.persist(cate);
    }
}
