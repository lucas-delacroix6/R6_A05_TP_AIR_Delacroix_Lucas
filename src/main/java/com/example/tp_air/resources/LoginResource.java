package com.example.tp_air.resources;

import com.example.tp_air.dto.LoginDTO;
import com.example.tp_air.dto.TokenDTO;
import com.example.tp_air.security.CustomHandler;
import com.example.tp_air.security.TokenStore;
import com.example.tp_air.security.principal.RolePrincipal;
import com.example.tp_air.security.principal.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

@Path("/login")
public class LoginResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginDTO request) {
        try {
            // 1. Authentification via JAAS (DbLoginModule vérifie en BDD)
            LoginContext lc = new LoginContext("MasterAnnonceLogin", new CustomHandler(request));
            lc.login();

            // 2. Récupération du UserPrincipal (username + userId)
            UserPrincipal principal = lc.getSubject()
                    .getPrincipals(UserPrincipal.class)
                    .iterator().next();

            // 3. Récupération du rôle depuis le Subject
            String role = lc.getSubject()
                    .getPrincipals(RolePrincipal.class)
                    .stream()
                    .findFirst()
                    .map(RolePrincipal::getName)
                    .orElse("ROLE_USER");

            // 4. Génération du token stateless (userId + username + role)
            String token = TokenStore.getInstance()
                    .generateToken(principal.getUserId(), principal.getName(), role);

            return Response.ok(new TokenDTO(token, TokenStore.getTtlSeconds())).build();

        } catch (LoginException e) {
            // 5. Credentials invalides → 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}