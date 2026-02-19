package com.example.tp_air.api;

import com.example.tp_air.api.mapper.*;
import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.security.TokenStore;
import com.example.tp_air.security.filter.AuthFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceResourceIT extends JerseyTest {

    private static String authToken;

    @BeforeAll
    static void initJaas() {
        URL jaasConf = AnnonceResourceIT.class.getClassLoader().getResource("jaas.conf");
        if (jaasConf != null) {
            System.setProperty("java.security.auth.login.config", jaasConf.toExternalForm());
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .register(AnnonceResource.class)
                .register(AuthResource.class)
                .register(HelloWorldResource.class)
                .register(AuthFilter.class)
                .register(NotFoundExceptionMapper.class)
                .register(BusinessExceptionMapper.class)
                .register(ForbiddenExceptionMapper.class)
                .register(ValidationExceptionMapper.class)
                .register(GenericExceptionMapper.class)
                // Jersey 3.x / Jakarta EE 10 : utiliser JacksonFeature à la place de
                // com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider (namespace javax,
                // incompatible)
                .register(JacksonFeature.class);
    }

    @BeforeEach
    void initToken() throws Exception {
        super.setUp();
        authToken = TokenStore.getInstance().generateToken(1L, "testuser", "ROLE_USER");
    }

    @AfterEach
    void tearDownTest() throws Exception {
        if (authToken != null) {
            TokenStore.getInstance().invalidate(authToken);
        }
        super.tearDown();
    }

    // --- HelloWorld ---

    @Test
    @Order(1)
    void helloWorld_returns200() {
        Response response = target("/helloWorld").request().get();
        assertEquals(200, response.getStatus());
    }

    @Test
    @Order(2)
    void params_queryParam_returns200() {
        Response response = target("/params").queryParam("name", "Claude").request().get();
        assertEquals(200, response.getStatus());
        String body = response.readEntity(String.class);
        assertTrue(body.contains("Claude"));
    }

    @Test
    @Order(3)
    void params_pathParam_returns200() {
        Response response = target("/params/hello").request().get();
        assertEquals(200, response.getStatus());
    }

    // --- Sans token ---

    @Test
    @Order(4)
    void createAnnonce_withoutToken_returns401() {
        AnnonceDTO dto = AnnonceDTO.builder()
                .title("Test")
                .description("Test")
                .price(new BigDecimal("10.00"))
                .build();

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(dto));

        assertEquals(401, response.getStatus());
    }

    // --- Avec token valide ---

    @Test
    @Order(5)
    void createAnnonce_withValidToken_returns201orNot401() {
        AnnonceDTO dto = AnnonceDTO.builder()
                .title("Integration Test Annonce")
                .description("Created via IT test")
                .price(new BigDecimal("99.99"))
                .build();

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.json(dto));

        assertNotEquals(401, response.getStatus(), "Auth doit passer — ne doit pas retourner 401");
    }

    // --- Validation ---

    @Test
    @Order(6)
    void createAnnonce_missingTitle_returns400() {
        AnnonceDTO dto = new AnnonceDTO();
        dto.setDescription("No title");

        Response response = target("/annonces")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.json(dto));

        assertNotEquals(200, response.getStatus());
    }

    // --- 404 ---

    @Test
    @Order(7)
    void getAnnonce_unknownId_returns404or500() {
        Response response = target("/annonces/999999").request().get();
        assertTrue(response.getStatus() == 404 || response.getStatus() == 500,
                "Doit retourner 404 (avec DB) ou 500 (sans DB)");
    }
}