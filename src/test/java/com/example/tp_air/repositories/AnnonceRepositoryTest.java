package com.example.tp_air.repositories;

import com.example.tp_air.models.*;
import com.example.tp_air.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AnnonceRepositoryTest {
    private EntityManager em;
    private AnnonceRepository repository;

    @BeforeEach
    void setUp() {
        em = JPAUtil.getEntityManager();
        repository = new AnnonceRepository(em);
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        em.close();
    }

    @Test
    void testSaveFindAndSearch() {
        // Création des entités parentes
        User author = new User();
        author.setUsername("dev_user_" + System.currentTimeMillis());
        author.setEmail("dev@test.fr");
        author.setPassword("secret123");
        em.persist(author);

        Category cat = new Category();
        cat.setLabel("Informatique");
        em.persist(cat);

        // Création de l'annonce
        Annonce a = new Annonce("Ordinateur Pro", "Super état", "Paris", "contact@pro.fr");
        a.setAuthor(author);
        a.setCategory(cat);
        a.setStatus(AnnonceStatus.DRAFT);

        repository.save(a);
        em.flush();

        // Test de la recherche JPQL
        List<Annonce> results = repository.search("Ordinateur", 1, 10, author.getId());

        assertFalse(results.isEmpty(), "L'annonce devrait être trouvée par mot-clé");
        assertEquals("Ordinateur Pro", results.getFirst().getTitle());
        assertEquals("Informatique", results.getFirst().getCategory().getLabel());
    }

    @Test
    @DisplayName("Niveau 3.b - Vérifie que les relations sont chargées (évite LazyInitializationException)")
    void testLazyLoadingPrevention() {
        User author = new User();
        author.setUsername("test_lazy_" + System.currentTimeMillis());
        author.setEmail("lazy@test.fr");
        author.setPassword("secret123");
        em.persist(author);

        Category cat = new Category();
        cat.setLabel("Test Lazy");
        em.persist(cat);

        Annonce a = new Annonce("Ordinateur Pro", "Super état", "Paris", "contact@pro.fr");
        a.setAuthor(author);
        a.setCategory(cat);
        a.setStatus(AnnonceStatus.DRAFT);

        repository.save(a);
        em.flush();
        Long savedId = a.getId();

        em.clear();

        Annonce found = repository.findById(savedId);

        assertNotNull(found, "L'annonce aurait dû être trouvée en base");

        em.detach(found);

        assertDoesNotThrow(() -> {
            String authorName = found.getAuthor().getUsername();
            String catLabel = found.getCategory().getLabel();

            System.out.println("Vérification Lazy OK : " + authorName + " / " + catLabel);

            assertEquals("Test Lazy", catLabel);
            assertNotNull(authorName);
        });
    }
}