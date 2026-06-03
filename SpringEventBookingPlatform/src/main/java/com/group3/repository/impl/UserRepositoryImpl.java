package com.group3.repository.impl;

import com.group3.pojo.User;
import com.group3.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private Environment env;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Session currentSession() {
        return Objects.requireNonNull(this.factory.getObject(), "SessionFactory is null").getCurrentSession();
    }

    @Override
    public User findUserById(Integer id) {
        if (id == null) {
            return null;
        }
        Session s = currentSession();
        return s.get(User.class, id);
    }

    @Override
    public User updateUser(User user) {
        Session s = currentSession();
        return (User) s.merge(user);
    }

    @Override
    public User findUserByEmail(String email) {
        Session s = currentSession();
        Query<User> q = s.createNamedQuery("User.findByEmail", User.class);
        q.setParameter("email", email);
        return q.uniqueResult();
    }

    @Override
    public List<User> getUsers(Map<String, String> params) {
        Session s = currentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<User> q = b.createQuery(User.class);
        Root<User> root = q.from(User.class);
        q.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isBlank()) {
                String t = kw.trim();
                Predicate byFullname = b.like(root.get("fullName"), "%" + t + "%"); 
                Predicate byEmail = b.like(root.get("email"), "%" + t + "%");
                predicates.add(b.or(byFullname, byEmail));
            }

            String roleId = params.get("roleId");
            if (roleId != null && !roleId.isBlank()) {
                predicates.add(b.equal(root.get("roleId").get("id"), Integer.parseInt(roleId)));
            }

            String statusId = params.get("statusId");
            if (statusId != null && !statusId.isBlank()) {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        q.orderBy(
                b.asc(root.get("statusId").get("id")),
                b.desc(root.get("id"))
        );
        Query<User> query = s.createQuery(q);

        int defaultPageSize = Integer.parseInt(this.env.getProperty("user.page_size"));
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
    public Long countUsers(Map<String, String> params) {
        Session s = currentSession();
        CriteriaBuilder b = s.getCriteriaBuilder();
        CriteriaQuery<Long> q = b.createQuery(Long.class);
        Root<User> root = q.from(User.class);

        q.select(b.count(root));

        List<Predicate> predicates = new ArrayList<>();
        if (params != null) {
            String kw = params.get("kw");
            if (kw != null && !kw.isBlank()) {
                String t = kw.trim();
                Predicate byFullname = b.like(root.get("fullName"), "%" + t + "%"); 
                Predicate byEmail = b.like(root.get("email"), "%" + t + "%");
                predicates.add(b.or(byFullname, byEmail));
            }

            String roleId = params.get("roleId");
            if (roleId != null && !roleId.isBlank()) {
                predicates.add(b.equal(root.get("roleId").get("id"), Integer.parseInt(roleId)));
            }

            String statusId = params.get("statusId");
            if (statusId != null && !statusId.isBlank()) {
                predicates.add(b.equal(root.get("statusId").get("id"), Integer.parseInt(statusId)));
            }
        }

        if (!predicates.isEmpty()) {
            q.where(predicates.toArray(Predicate[]::new));
        }

        return s.createQuery(q).getSingleResult();
    }

    @Override
    public boolean existEmail(String email) {
        Session s = currentSession();
        Query<User> q = s.createNamedQuery("User.findByEmail", User.class);
        q.setParameter("email", email);
        return q.uniqueResult() != null;
    }

    @Override
    public Long count() {
        Session s = currentSession();
        Query<Long> q = s.createQuery("select count(*) from User", Long.class);
        return q.uniqueResult();
    }

    @Override
    public User addUser(User u) {
        Session s = currentSession();
        s.persist(u);

        return u;
    }

    @Override
    public boolean authenticate(String email, String password) {
        User user = findUserByEmail(email);
        if (user == null) {
            return false;
        }
        return this.bCryptPasswordEncoder.matches(password, user.getPassword());
    }

    @Override
    public boolean changePassword(Integer userId, String newEncryptedPassword) {
        if (userId == null || newEncryptedPassword == null || newEncryptedPassword.isEmpty()) {
            return false;
        }
        Session session = this.currentSession();
        String hql = "UPDATE User u SET u.password = :neq WHERE u.id = :id";
        int rowsAffected = session.createMutationQuery(hql)
                .setParameter("nep", newEncryptedPassword)
                .setParameter("id", userId)
                .executeUpdate();

        return rowsAffected > 0;
    }
}
