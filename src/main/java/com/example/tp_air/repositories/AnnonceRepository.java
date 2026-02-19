package com.example.tp_air.repositories;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class AnnonceRepository {

    private static final Logger log = LoggerFactory.getLogger(AnnonceRepository.class);

    public List<Annonce> findAll(int page, int size) {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            TypedQuery<Annonce> q = em.createQuery(
                    "SELECT a FROM Annonce a JOIN FETCH a.author ORDER BY a.createdAt DESC",
                    Annonce.class
            );
            q.setFirstResult(page * size);
            q.setMaxResults(size);
            return q.getResultList();
        }
    }

    public long countAll() {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            return em.createQuery("SELECT COUNT(a) FROM Annonce a", Long.class)
                    .getSingleResult();
        }
    }

    public Optional<Annonce> findById(Long id) {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            TypedQuery<Annonce> q = em.createQuery(
                    "SELECT a FROM Annonce a JOIN FETCH a.author WHERE a.id = :id",
                    Annonce.class
            );
            q.setParameter("id", id);
            List<Annonce> results = q.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
        }
    }

    public Annonce save(Annonce annonce) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Annonce managed = em.merge(annonce);
            em.getTransaction().commit();
            log.debug("Saved annonce id={}", managed.getId());
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Annonce update(Annonce annonce) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Annonce managed = em.merge(annonce);
            em.getTransaction().commit();
            log.debug("Updated annonce id={}", managed.getId());
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, id);
            if (annonce != null) {
                em.remove(annonce);
            }
            em.getTransaction().commit();
            log.debug("Deleted annonce id={}", id);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Reload a managed instance for optimistic locking (@Version)
     */
    public Optional<Annonce> findByIdForUpdate(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            em.getTransaction().begin();
            Annonce annonce = em.find(Annonce.class, id,
                    jakarta.persistence.LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            em.getTransaction().commit();
            return Optional.ofNullable(annonce);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}