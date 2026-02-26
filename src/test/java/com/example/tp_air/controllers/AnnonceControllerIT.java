package com.example.tp_air.controllers;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.models.User;
import com.example.tp_air.repositories.AnnonceRepository;
import com.example.tp_air.repositories.UserRepository;
import com.example.tp_air.security.CustomUserDetails;
import com.example.tp_air.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AnnonceControllerIT {

        @Container
        public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                        .withDatabaseName("tp_air_test")
                        .withUsername("test")
                        .withPassword("test");

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
                registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
                registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
                registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        }

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private AnnonceRepository annonceRepository;

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        @Autowired
        private ObjectMapper objectMapper;

        private User testUser;
        private String token;

        @BeforeEach
        void setUp() {
                annonceRepository.deleteAll();
                userRepository.deleteAll();

                testUser = User.builder()
                                .username("john")
                                .password("pwd123")
                                .email("john@example.com")
                                .role(User.Role.ROLE_USER)
                                .build();
                testUser = userRepository.save(testUser);

                CustomUserDetails userDetails = new CustomUserDetails(
                                testUser.getId(),
                                testUser.getUsername(),
                                testUser.getPassword(),
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                token = jwtTokenProvider.generateToken(auth);
        }

        @Test
        void testGetAnnonce_WithoutAuth_Throws401() throws Exception {
                mockMvc.perform(post("/api/annonces")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void testCreateAnnonce_WithAuth_Returns201() throws Exception {
                AnnonceDTO dto = AnnonceDTO.builder()
                                .title("New Annonce")
                                .description("A great new annonce")
                                .price(new BigDecimal("99.99"))
                                .category("BOOKS")
                                .build();

                mockMvc.perform(post("/api/annonces")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value("New Annonce"))
                                .andExpect(jsonPath("$.status").value("DRAFT"))
                                .andExpect(jsonPath("$.authorId").value(testUser.getId()));
        }
}
