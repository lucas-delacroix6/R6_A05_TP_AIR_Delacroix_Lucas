package com.example.tp_air.filters;

import com.example.tp_air.security.TokenStore;
import com.example.tp_air.security.filter.AuthFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthFilterTest {

    @BeforeAll
    static void initJaas() {
        URL jaasConf = AuthFilterTest.class.getClassLoader().getResource("jaas.conf");
        if (jaasConf != null) {
            System.setProperty("java.security.auth.login.config", jaasConf.toExternalForm());
        }
    }

    @Mock
    private ContainerRequestContext requestContext;

    private AuthFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthFilter();
    }

    @Test
    void missingAuthHeader_returns401() throws Exception {
        when(requestContext.getHeaderString("Authorization")).thenReturn(null);

        filter.filter(requestContext);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(captor.capture());
        assertEquals(401, captor.getValue().getStatus());
    }

    @Test
    void invalidBearerPrefix_returns401() throws Exception {
        when(requestContext.getHeaderString("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.filter(requestContext);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(captor.capture());
        assertEquals(401, captor.getValue().getStatus());
    }

    @Test
    void invalidToken_returns401() throws Exception {
        when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer token-invalide-xyz");

        filter.filter(requestContext);

        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(requestContext).abortWith(captor.capture());
        assertEquals(401, captor.getValue().getStatus());
    }

    @Test
    void validToken_doesNotAbort_andSetsUserId() throws Exception {
        String token = TokenStore.getInstance().generateToken(42L, "john", "ROLE_USER");

        when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer " + token);

        filter.filter(requestContext);

        verify(requestContext, never()).abortWith(any());

        verify(requestContext).setProperty("userId", 42L);
        verify(requestContext).setProperty("username", "john");

        TokenStore.getInstance().invalidate(token);
    }
}