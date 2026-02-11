package com.example.tp_air.repositories;

import com.example.tp_air.models.User;
import jakarta.persistence.EntityManager;

public class UserRepository {
    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }
}