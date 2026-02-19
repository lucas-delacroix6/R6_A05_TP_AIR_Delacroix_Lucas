package com.example.tp_air.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton provider for JPA EntityManagerFactory.
 * Supports switching between production (PostgreSQL) and test (H2) PU.
 */
public class JPAUtil {

    private static final Logger log = LoggerFactory.getLogger(JPAUtil.class);
    private static EntityManagerFactory emf;
    private static String persistenceUnitName = "masterannoncePU";

    private JPAUtil() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            log.info("Initializing EntityManagerFactory for PU: {}", persistenceUnitName);
            emf = Persistence.createEntityManagerFactory(persistenceUnitName);
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void setPersistenceUnitName(String name) {
        persistenceUnitName = name;
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}