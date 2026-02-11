package com.example.tp_air.services;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.CategoryRepository;
import com.example.tp_air.repositories.UserRepository;
import com.example.tp_air.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class AnnonceService {
    private final Validator validator;
    private AnnonceRepository annonceRepository;

    public AnnonceService() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public AnnonceService(AnnonceRepository annonceRepository) {
        this();
        this.annonceRepository = annonceRepository;
    }

    private AnnonceRepository getAnnonceRepository(EntityManager em) {
        if (this.annonceRepository != null) return this.annonceRepository;
        return new AnnonceRepository(em);
    }

    private <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                sb.append(violation.getMessage()).append(". ");
            }
            throw new RuntimeException(sb.toString());
        }
    }

    public void createAnnonce(Annonce annonce) {
        validate(annonce);
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                annonce.setStatus(AnnonceStatus.DRAFT);
                getAnnonceRepository(em).save(annonce);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    public void updateAnnonce(Annonce annonce) {
        validate(annonce);
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                getAnnonceRepository(em).update(annonce);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    public void publishAnnonce(Long id, User currentUser) {
        if (isAuthor(id, currentUser)) {
            updateStatus(id, AnnonceStatus.PUBLISHED);
        }
    }

    public void archiveAnnonce(Long id, User currentUser) {
        if (isAuthor(id, currentUser)) {
            updateStatus(id, AnnonceStatus.ARCHIVED);
        }
    }

    private void updateStatus(Long id, AnnonceStatus newStatus) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                AnnonceRepository repo = getAnnonceRepository(em);
                Annonce annonce = repo.findById(id);
                if (annonce != null) {
                    annonce.setStatus(newStatus);
                    repo.update(annonce);
                }
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
            }
        }
    }

    public void deleteAnnonce(Annonce annonce) {
        validate(annonce);
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Annonce managedAnnonce = em.merge(annonce);
                getAnnonceRepository(em).delete(managedAnnonce);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            }
        }
    }

    public List<Annonce> searchAnnonces(String keyword, int page, int size, User currentUser) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long userId = (currentUser != null) ? currentUser.getId() : -1L;
            return getAnnonceRepository(em).search(keyword, page, size, userId);
        }
    }

    public long countAnnonces(String keyword, User currentUser) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long userId = (currentUser != null) ? currentUser.getId() : -1L;
            return getAnnonceRepository(em).countSearch(keyword, userId);
        }
    }

    public Annonce findAnnonceById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return getAnnonceRepository(em).findById(id);
        }
    }

    public List<Annonce> findAllAnnonces(User currentUser) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Long userId = (currentUser != null) ? currentUser.getId() : -1L;
            return getAnnonceRepository(em).findAll(userId);
        }
    }

    public Category findCategoryById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return new CategoryRepository(em).findById(id);
        }
    }

    public List<Category> findAllCategories() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return new CategoryRepository(em).findAll();
        }
    }

    public User findUserById(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return new UserRepository(em).findById(id);
        }
    }

    public List<User> findAllUsers() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }

    public void createUser(User user) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(user);
                tx.commit();
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    public boolean isAuthor(Long annonceId, User user) {
        if (user == null) return false;
        Annonce annonce = findAnnonceById(annonceId);
        return annonce != null && annonce.getAuthor() != null &&
                annonce.getAuthor().getId().equals(user.getId());
    }
}