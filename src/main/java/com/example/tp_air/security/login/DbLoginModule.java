package com.example.tp_air.security.login;

import com.example.tp_air.models.User;
import com.example.tp_air.repositories.UserRepository;
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
 * JAAS LoginModule for username/password authentication against the database.
 */
public class DbLoginModule implements LoginModule {

    private static final Logger log = LoggerFactory.getLogger(DbLoginModule.class);

    private Subject subject;
    private CallbackHandler callbackHandler;

    private boolean authenticated = false;
    private User authenticatedUser;

    private UserRepository userRepository = new UserRepository();

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
                           Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login() throws LoginException {
        NameCallback nameCallback = new NameCallback("username:");
        PasswordCallback passwordCallback = new PasswordCallback("password:", false);

        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Error handling callbacks: " + e.getMessage());
        }

        String username = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        passwordCallback.clearPassword();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new FailedLoginException("Invalid credentials");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new FailedLoginException("Invalid credentials");
        }

        authenticatedUser = user;
        authenticated = true;
        log.info("JAAS login success for user={}", username);
        return true;
    }

    @Override
    public boolean commit() {
        if (!authenticated) return false;

        subject.getPrincipals().add(
                new UserPrincipal(authenticatedUser.getUsername(), authenticatedUser.getId())
        );
        subject.getPrincipals().add(
                new RolePrincipal(authenticatedUser.getRole().name())
        );
        return true;
    }

    @Override
    public boolean abort() {
        authenticated = false;
        authenticatedUser = null;
        return true;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().removeIf(p -> p instanceof UserPrincipal || p instanceof RolePrincipal);
        return true;
    }

    public void setUserRepository(UserRepository repo) {
        this.userRepository = repo;
    }
}