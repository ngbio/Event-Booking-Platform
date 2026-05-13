package com.group3.repository.impl;

import com.group3.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.group3.repository.UserRepository;

import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author THUAN
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository{
    @Autowired
    private LocalSessionFactoryBean factory;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        Session session = this.factory.getObject().getCurrentSession();
        Query q = session.createNamedQuery("User.findByUsername", User.class);
        q.setParameter("username", username);

        return (User) q.getSingleResult();

    }

    @Override
    public User addUser(User u) {
        Session session = this.factory.getObject().getCurrentSession();
        session.persist(u);
        
        return u;
    }

    @Override
    public boolean authenticate(String username, String password) {
        User u = this.getUserByUsername(username);

        return this.passwordEncoder.matches(password, u.getPassword());
    }
}
