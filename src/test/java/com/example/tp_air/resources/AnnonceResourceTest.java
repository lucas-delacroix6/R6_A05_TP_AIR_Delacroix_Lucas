package com.example.tp_air.resources;

import com.example.tp_air.config.GlobalExceptionMapper;
import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.services.AnnonceService;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

public class AnnonceResourceTest extends JerseyTest {

    private AnnonceService mockService;

    @Override
    protected Application configure() {
        mockService = Mockito.mock(AnnonceService.class);

        return new ResourceConfig()
                .register(AnnonceResource.class)
                .register(JacksonFeature.class)
                .register(GlobalExceptionMapper.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(mockService).to(AnnonceService.class);
                    }
                });
    }

    @Test
    void testCreateAnnonce_Returns201() {
        AnnonceDTO mockResponse = new AnnonceDTO();
        mockResponse.setId(1L);

        Mockito.when(mockService.create(any(AnnonceDTO.class), nullable(Long.class)))
                .thenReturn(mockResponse);

        // Requête
        AnnonceDTO input = new AnnonceDTO();
        input.setTitle("Titre Test");
        input.setDescription("Description Test");
        input.setPrice(new java.math.BigDecimal("10.00"));

        Response response = target("/annonces")
                .request()
                .post(Entity.entity(input, MediaType.APPLICATION_JSON));

        // Vérification
        assertEquals(201, response.getStatus());
    }

    @Test
    void testGetUnknownAnnonce_Returns404() {
        // Règle du Mock : Si on te demande n'importe quel ID, lance l'erreur métier
        Mockito.when(mockService.findById(anyLong()))
                .thenThrow(new RuntimeException("404: Not Found"));

        // Requête
        Response response = target("/annonces/99999").request().get();

        // Vérification
        assertEquals(404, response.getStatus());
    }
}
