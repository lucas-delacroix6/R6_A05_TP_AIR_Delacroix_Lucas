package com.example.tp_air.security.filter;

import com.example.tp_air.security.principal.RolePrincipal;
import com.example.tp_air.security.principal.UserPrincipal;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

/**
 * JAX-RS filter that verifies the Bearer token on each protected request.
 * Reconstructs Subject via JAAS (TokenLoginModule) and attaches it as SecurityContext.
 * <p>
 * Apply @Secured annotation on resources to enable.
 */
@Provider
@Secured
public class AuthFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String authHeader = ctx.getHeaderString(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header");
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"UNAUTHORIZED\",\"messages\":[\"Missing or invalid token\"]}")
                    .type("application/json")
                    .build());
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            CallbackHandler handler = callbacks -> {
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        ((NameCallback) cb).setName(token);
                    }
                }
            };

            LoginContext lc = new LoginContext("MasterAnnonceToken", handler);
            lc.login();
            Subject subject = lc.getSubject();

            // Extract principals
            Optional<UserPrincipal> userPrincipal = subject.getPrincipals(UserPrincipal.class)
                    .stream().findFirst();

            if (userPrincipal.isEmpty()) {
                abort401(ctx, "Invalid token");
                return;
            }

            UserPrincipal up = userPrincipal.get();

            // Attach custom SecurityContext to the request
            ctx.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() { return up; }

                @Override
                public boolean isUserInRole(String role) {
                    return subject.getPrincipals(RolePrincipal.class)
                            .stream().anyMatch(rp -> rp.getName().equals(role));
                }

                @Override
                public boolean isSecure() { return ctx.getSecurityContext().isSecure(); }

                @Override
                public String getAuthenticationScheme() { return "Bearer"; }
            });

            // Store subject as property for service layer access
            ctx.setProperty("subject", subject);
            ctx.setProperty("userId", up.getUserId());
            ctx.setProperty("username", up.getName());

        } catch (LoginException e) {
            log.warn("Token authentication failed: {}", e.getMessage());
            abort401(ctx, "Invalid or expired token");
        }
    }

    private void abort401(ContainerRequestContext ctx, String message) {
        ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\":\"UNAUTHORIZED\",\"messages\":[\"" + message + "\"]}")
                .type("application/json")
                .build());
    }
}