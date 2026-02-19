package com.example.tp_air.repositories;

import com.example.tp_air.models.User;
import com.example.tp_air.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    public Optional<User> findById(Long id) {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            return Optional.ofNullable(em.find(User.class, id));
        }
    }

    public Optional<User> findByUsername(String username) {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            return Optional.of(
                    em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public User save(User user) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            User managed = em.merge(user);
            em.getTransaction().commit();
            log.debug("Saved user id={}", managed.getId());
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}