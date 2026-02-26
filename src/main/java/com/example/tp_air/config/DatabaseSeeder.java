package com.example.tp_air.config;

import com.example.tp_air.models.Annonce;
import com.example.tp_air.models.AnnonceStatus;
import com.example.tp_air.models.Category;
import com.example.tp_air.models.User;
import com.example.tp_air.models.User.Role;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.CategoryRepository;
import com.example.tp_air.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnnonceRepository annonceRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository,
            CategoryRepository categoryRepository,
            AnnonceRepository annonceRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.annonceRepository = annonceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            logger.info("Initializing database with test data...");
            seedData();
        } else {
            logger.info("Database already contains data. Seeding skipped.");
        }
    }

    private void seedData() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@test.com")
                .role(Role.ROLE_ADMIN)
                .build();

        User john = User.builder()
                .username("john")
                .password(passwordEncoder.encode("password"))
                .email("john@test.com")
                .role(Role.ROLE_USER)
                .build();

        User jane = User.builder()
                .username("jane")
                .password(passwordEncoder.encode("password"))
                .email("jane@test.com")
                .role(Role.ROLE_USER)
                .build();

        userRepository.saveAll(Arrays.asList(admin, john, jane));
        logger.info("Users initialized: admin, john, jane");

        Category catElectronics = new Category();
        catElectronics.setLabel("Electronique");

        Category catVehicles = new Category();
        catVehicles.setLabel("Vehicules");

        Category catFurniture = new Category();
        catFurniture.setLabel("Meubles");

        Category catServices = new Category();
        catServices.setLabel("Services");

        Category catTest = new Category();
        catTest.setLabel("Test");

        categoryRepository.saveAll(Arrays.asList(catElectronics, catVehicles, catFurniture, catServices, catTest));
        logger.info("Categories initialized.");

        Annonce ann1 = new Annonce();
        ann1.setTitle("MacBook Pro M2");
        ann1.setDescription("A vendre MacBook Pro M2 en parfait etat.");
        ann1.setPrice(BigDecimal.valueOf(1200.0));
        ann1.setCategory(catElectronics);
        ann1.setAuthor(john);
        ann1.setStatus(AnnonceStatus.PUBLISHED);

        Annonce ann2 = new Annonce();
        ann2.setTitle("Canap\u00e9 en cuir");
        ann2.setDescription("Canap\u00e9 3 places tres confortable, peu servi.");
        ann2.setPrice(BigDecimal.valueOf(350.0));
        ann2.setCategory(catFurniture);
        ann2.setAuthor(jane);
        ann2.setStatus(AnnonceStatus.DRAFT);

        Annonce ann3 = new Annonce();
        ann3.setTitle("Velo de montagne");
        ann3.setDescription("Super velo pour vos balades en foret.");
        ann3.setPrice(BigDecimal.valueOf(150.0));
        ann3.setCategory(catVehicles);
        ann3.setAuthor(john);
        ann3.setStatus(AnnonceStatus.PUBLISHED);

        annonceRepository.saveAll(Arrays.asList(ann1, ann2, ann3));
        logger.info("Annoncements initialized.");
        logger.info("Database seeding completed successfully!");
    }
}
