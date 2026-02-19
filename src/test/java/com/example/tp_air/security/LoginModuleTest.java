package com.example.tp_air.security;

import com.example.tp_air.models.User;
import com.example.tp_air.repositories.UserRepository;
import com.example.tp_air.security.login.DbLoginModule;
import com.example.tp_air.security.login.TokenLoginModule;
import com.example.tp_air.security.principal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JAAS LoginModules.
 */
@ExtendWith(MockitoExtension.class)
class LoginModuleTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("john")
                .password("secret")
                .email("john@test.com")
                .role(User.Role.ROLE_USER)
                .build();
        try {
            var f = User.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(testUser, 1L);
        } catch (Exception ignored) {}
    }

    // --- DbLoginModule ---

    @Test
    void dbLoginModule_validCredentials_loginSucceeds() throws Exception {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        Subject subject = new Subject();
        CallbackHandler handler = makeHandler("john", "secret");

        DbLoginModule module = new DbLoginModule();
        module.setUserRepository(userRepository);
        module.initialize(subject, handler, new HashMap<>(), new HashMap<>());

        assertTrue(module.login());
        assertTrue(module.commit());

        assertFalse(subject.getPrincipals(UserPrincipal.class).isEmpty());
        assertEquals("john", subject.getPrincipals(UserPrincipal.class).iterator().next().getName());
    }

    @Test
    void dbLoginModule_wrongPassword_throwsFailedLoginException() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(testUser));

        Subject subject = new Subject();
        CallbackHandler handler = makeHandler("john", "wrong password");

        DbLoginModule module = new DbLoginModule();
        module.setUserRepository(userRepository);
        module.initialize(subject, handler, new HashMap<>(), new HashMap<>());

        assertThrows(FailedLoginException.class, module::login);
    }

    @Test
    void dbLoginModule_unknownUser_throwsFailedLoginException() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        Subject subject = new Subject();
        CallbackHandler handler = makeHandler("nobody", "pass");

        DbLoginModule module = new DbLoginModule();
        module.setUserRepository(userRepository);
        module.initialize(subject, handler, new HashMap<>(), new HashMap<>());

        assertThrows(FailedLoginException.class, module::login);
    }

    // --- TokenLoginModule ---

    @Test
    void tokenLoginModule_validToken_loginSucceeds() throws Exception {
        TokenStore store = TokenStore.getInstance();
        String token = store.generateToken(1L, "john", "ROLE_USER");

        Subject subject = new Subject();
        CallbackHandler handler = makeTokenHandler(token);

        TokenLoginModule module = new TokenLoginModule();
        module.setTokenStore(store);
        module.initialize(subject, handler, new HashMap<>(), new HashMap<>());

        assertTrue(module.login());
        assertTrue(module.commit());
        assertFalse(subject.getPrincipals(UserPrincipal.class).isEmpty());

        store.invalidate(token);
    }

    @Test
    void tokenLoginModule_invalidToken_throwsFailedLoginException() {
        TokenStore store = TokenStore.getInstance();

        Subject subject = new Subject();
        CallbackHandler handler = makeTokenHandler("invalid-token-xyz");

        TokenLoginModule module = new TokenLoginModule();
        module.setTokenStore(store);
        module.initialize(subject, handler, new HashMap<>(), new HashMap<>());

        assertThrows(FailedLoginException.class, module::login);
    }

    // --- Helpers ---

    private CallbackHandler makeHandler(String username, String password) {
        return callbacks -> {
            for (Callback cb : callbacks) {
                if (cb instanceof NameCallback) ((NameCallback) cb).setName(username);
                else if (cb instanceof PasswordCallback) ((PasswordCallback) cb).setPassword(password.toCharArray());
            }
        };
    }

    private CallbackHandler makeTokenHandler(String token) {
        return callbacks -> {
            for (Callback cb : callbacks) {
                if (cb instanceof NameCallback) ((NameCallback) cb).setName(token);
            }
        };
    }
}