package com.example.tp_air.security.login;

import com.example.tp_air.security.TokenStore;
import com.example.tp_air.security.principal.RolePrincipal;
import com.example.tp_air.security.principal.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * JAAS LoginModule for token-based authentication (stateless).
 * Reconstructs user identity from a token on each request.
 */
public class TokenLoginModule implements LoginModule {

    private static final Logger log = LoggerFactory.getLogger(TokenLoginModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean authenticated = false;
    private TokenStore.TokenInfo tokenInfo;

    private TokenStore tokenStore = TokenStore.getInstance();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        // Reuse NameCallback to carry the token
        NameCallback tokenCallback = new NameCallback("token:");

        try {
            callbackHandler.handle(new Callback[]{tokenCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Error handling callbacks: " + e.getMessage());
        }

        String token = tokenCallback.getName();
        if (token == null || token.isBlank()) {
            throw new FailedLoginException("Token is missing");
        }

        Optional<TokenStore.TokenInfo> infoOpt = tokenStore.validate(token);
        if (infoOpt.isEmpty()) {
            throw new FailedLoginException("Token is invalid or expired");
        }

        tokenInfo = infoOpt.get();
        authenticated = true;
        log.debug("Token auth success for user={}", tokenInfo.username());
        return true;
    }

    @Override
    public boolean commit() {
        if (!authenticated) return false;
        subject.getPrincipals().add(new UserPrincipal(tokenInfo.username(), tokenInfo.userId()));
        subject.getPrincipals().add(new RolePrincipal(tokenInfo.role()));
        return true;
    }

    @Override
    public boolean abort() {
        authenticated = false;
        tokenInfo = null;
        return true;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().removeIf(p -> p instanceof UserPrincipal || p instanceof RolePrincipal);
        return true;
    }

    public void setTokenStore(TokenStore store) {
        this.tokenStore = store;
    }
}