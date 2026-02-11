package com.example.tp_air.repositories;

import com.example.tp_air.models.Category;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CategoryRepository {
    private final EntityManager em;

    public CategoryRepository(EntityManager em) {
        this.em = em;
    }

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    public Category findById(Long id) {
        return em.find(Category.class, id);
    }
}