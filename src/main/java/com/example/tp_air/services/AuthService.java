package com.example.tp_air.services;

import com.example.tp_air.dto.LoginDTO;
import com.example.tp_air.dto.TokenDTO;
import com.example.tp_air.models.User;
import com.example.tp_air.security.TokenStore;
import com.example.tp_air.security.principal.RolePrincipal;
import com.example.tp_air.security.principal.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public TokenDTO login(LoginDTO credentials) throws LoginException {
        Subject subject = getSubject(credentials);

        // Extract user info from Subject
        UserPrincipal userPrincipal =
                subject.getPrincipals(UserPrincipal.class)
                        .stream().findFirst()
                        .orElseThrow(() -> new LoginException("No UserPrincipal in Subject"));

        String role = subject.getPrincipals(RolePrincipal.class)
                .stream().findFirst()
                .map(RolePrincipal::getName)
                .orElse(User.Role.ROLE_USER.name());

        String token = TokenStore.getInstance().generateToken(
                userPrincipal.getUserId(), userPrincipal.getName(), role
        );

        log.info("Login successful for user={}", userPrincipal.getName());
        return new TokenDTO(token, TokenStore.getTtlSeconds());
    }

    private static Subject getSubject(LoginDTO credentials) throws LoginException {
        CallbackHandler handler = callbacks -> {
            for (Callback cb : callbacks) {
                if (cb instanceof NameCallback) {
                    ((NameCallback) cb).setName(credentials.getUsername());
                } else if (cb instanceof PasswordCallback) {
                    ((PasswordCallback) cb).setPassword(credentials.getPassword().toCharArray());
                }
            }
        };

        LoginContext lc = new LoginContext("MasterAnnonceLogin", handler);
        lc.login();

        return lc.getSubject();
    }
}