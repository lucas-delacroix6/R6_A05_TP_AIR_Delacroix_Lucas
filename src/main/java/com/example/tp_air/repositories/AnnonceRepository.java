package com.example.tp_air.repositories;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AnnonceRepository {

    private final EntityManager em;

    public AnnonceRepository(EntityManager em) {
        this.em = em;
    }

    public void save(Annonce annonce) {
        em.persist(annonce);
    }

    public void update(Annonce annonce) {
        em.merge(annonce);
    }

    public void delete(Annonce annonce) {
        em.remove(annonce);
    }

    public Annonce findById(Long id) {
        try {
            return em.createQuery(
                    "SELECT a FROM Annonce a " +
                    "LEFT JOIN FETCH a.category " +
                    "LEFT JOIN FETCH a.author " +
                    "WHERE a.id = :id", Annonce.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Annonce> findAll(Long currentUserId) {
        return em.createQuery(
                        "SELECT a FROM Annonce a " +
                                "LEFT JOIN FETCH a.author " +
                                "LEFT JOIN FETCH a.category " +
                                "WHERE a.status = 'PUBLISHED' OR a.author.id = :userId", Annonce.class)
                .setParameter("userId", currentUserId)
                .getResultList();
    }

    public List<Annonce> search(String keyword, int page, int size, Long currentUserId) {
        String safeKeyword = (keyword == null) ? "" : keyword.toLowerCase();

        String jpql = "SELECT a FROM Annonce a LEFT JOIN FETCH a.category LEFT JOIN FETCH a.author " +
                "WHERE (LOWER(a.title) LIKE :kw OR LOWER(a.description) LIKE :kw) " +
                "AND (a.status = 'PUBLISHED' OR a.author.id = :userId) " +
                "ORDER BY a.date DESC";

        TypedQuery<Annonce> query = em.createQuery(jpql, Annonce.class);
        query.setParameter("kw", "%" + safeKeyword + "%");
        query.setParameter("userId", currentUserId);

        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countSearch(String keyword, Long userId) {
        String jpql = "SELECT COUNT(a) FROM Annonce a " +
                "WHERE (LOWER(a.title) LIKE :kw OR LOWER(a.description) LIKE :kw) " +
                "AND (a.status = 'PUBLISHED' OR a.author.id = :userId)";

        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(jpql, Long.class)
                    .setParameter("kw", "%" + (keyword == null ? "" : keyword.toLowerCase()) + "%")
                    .setParameter("userId", userId)
                    .getSingleResult();
        }
    }
}