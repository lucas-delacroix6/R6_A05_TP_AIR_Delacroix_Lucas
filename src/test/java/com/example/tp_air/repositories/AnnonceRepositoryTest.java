package com.example.tp_air.repositories;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.utils.JPAUtil;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AnnonceRepository using an H2 in-memory database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceRepositoryTest {

    private static AnnonceRepository repository;

    @BeforeAll
    static void setup() {
        JPAUtil.setPersistenceUnitName("masterannonceTestPU");
        repository = new AnnonceRepository();
        TestDataLoader.load();
    }

    @AfterAll
    static void tearDown() {
        TestDataLoader.cleanup();
        JPAUtil.close();
    }

    @Test
    @Order(1)
    void testFindAll_pagination_page0() {
        List<Annonce> results = repository.findAll(0, 2);
        assertEquals(2, results.size(), "Page 0 with size 2 should return 2 results");
    }

    @Test
    @Order(2)
    void testFindAll_pagination_page1() {
        List<Annonce> results = repository.findAll(1, 2);
        assertEquals(1, results.size(), "Page 1 with size 2 should return 1 remaining result");
    }

    @Test
    @Order(3)
    void testCountAll() {
        long count = repository.countAll();
        assertEquals(3, count, "Should count 3 annonces");
    }

    @Test
    @Order(4)
    void testFindById_existing() {
        Optional<Annonce> result = repository.findById(TestDataLoader.ANNONCE_DRAFT.getId());
        assertTrue(result.isPresent());
        assertEquals("Draft Annonce", result.get().getTitle());
    }

    @Test
    @Order(5)
    void testFindById_notFound() {
        Optional<Annonce> result = repository.findById(999999L);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(6)
    void testSave_and_findById() {
        Annonce annonce = Annonce.builder()
                .title("New Test Annonce")
                .description("Created in test")
                .status(Annonce.Status.DRAFT)
                .author(TestDataLoader.USER_1)
                .build();

        Annonce saved = repository.save(annonce);
        assertNotNull(saved.getId());

        Optional<Annonce> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("New Test Annonce", found.get().getTitle());

        // Cleanup
        repository.delete(saved.getId());
    }

    @Test
    @Order(7)
    void testDelete() {
        Annonce annonce = Annonce.builder()
                .title("To Delete")
                .description("Will be deleted")
                .status(Annonce.Status.ARCHIVED)
                .author(TestDataLoader.USER_1)
                .build();
        Annonce saved = repository.save(annonce);
        Long id = saved.getId();

        repository.delete(id);

        assertFalse(repository.findById(id).isPresent());
    }
}