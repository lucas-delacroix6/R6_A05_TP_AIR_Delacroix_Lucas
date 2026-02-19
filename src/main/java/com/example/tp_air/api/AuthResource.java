package com.example.tp_air.api;

import com.example.tp_air.dto.ErrorResponse;
import com.example.tp_air.dto.LoginDTO;
import com.example.tp_air.dto.TokenDTO;
import com.example.tp_air.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthResource {

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);
    private final AuthService authService = new AuthService();

    /**
     * POST /api/login
     * Body: { "username": "john", "password": "secret" }
     * Returns: { "token": "uuid", "expiresIn": 3600 }
     */
    @POST
    @Operation(summary = "Authenticate and get token")
    public Response login(@Valid LoginDTO credentials) {
        try {
            TokenDTO token = authService.login(credentials);
            return Response.ok(token).build();
        } catch (FailedLoginException e) {
            log.warn("Login failed for user={}", credentials.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("AUTHENTICATION_FAILED", "Invalid credentials"))
                    .build();
        } catch (LoginException e) {
            log.error("Login error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("LOGIN_ERROR", e.getMessage()))
                    .build();
        }
    }
}