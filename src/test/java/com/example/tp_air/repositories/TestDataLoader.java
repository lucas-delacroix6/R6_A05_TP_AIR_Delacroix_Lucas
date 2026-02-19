package com.example.tp_air.repositories;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.User;
import com.example.tp_air.utils.JPAUtil;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;

/**
 * Loads test data into the H2 in-memory database before test scenarios.
 */
public class TestDataLoader {

    public static User USER_1;
    public static User USER_2;
    public static Annonce ANNONCE_DRAFT;
    public static Annonce ANNONCE_PUBLISHED;
    public static Annonce ANNONCE_ARCHIVED;

    public static void load() {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            em.getTransaction().begin();

            USER_1 = User.builder()
                    .username("testuser1")
                    .password("password")
                    .email("user1@test.com")
                    .role(User.Role.ROLE_USER)
                    .build();

            USER_2 = User.builder()
                    .username("testuser2")
                    .password("password")
                    .email("user2@test.com")
                    .role(User.Role.ROLE_USER)
                    .build();

            em.persist(USER_1);
            em.persist(USER_2);

            ANNONCE_DRAFT = Annonce.builder()
                    .title("Draft Annonce")
                    .description("A draft annonce")
                    .price(new BigDecimal("100.00"))
                    .category("Electronics")
                    .status(Annonce.Status.DRAFT)
                    .author(USER_1)
                    .build();

            ANNONCE_PUBLISHED = Annonce.builder()
                    .title("Published Annonce")
                    .description("A published annonce")
                    .price(new BigDecimal("200.00"))
                    .category("Books")
                    .status(Annonce.Status.PUBLISHED)
                    .author(USER_1)
                    .build();

            ANNONCE_ARCHIVED = Annonce.builder()
                    .title("Archived Annonce")
                    .description("An archived annonce")
                    .price(new BigDecimal("50.00"))
                    .category("Misc")
                    .status(Annonce.Status.ARCHIVED)
                    .author(USER_1)
                    .build();

            em.persist(ANNONCE_DRAFT);
            em.persist(ANNONCE_PUBLISHED);
            em.persist(ANNONCE_ARCHIVED);

            em.getTransaction().commit();
        }
    }

    public static void cleanup() {
        try (EntityManager em = JPAUtil.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Annonce").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        }
    }
}