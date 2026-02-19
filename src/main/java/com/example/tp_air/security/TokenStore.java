package com.example.tp_air.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token store (stateless approach).
 * Maps token -> TokenInfo (userId, username, role, expiration).
 */
public class TokenStore {

    private static final Logger log = LoggerFactory.getLogger(TokenStore.class);
    private static final TokenStore INSTANCE = new TokenStore();
    private static final int TTL_SECONDS = 3600;

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    private TokenStore() {}

    public static TokenStore getInstance() { return INSTANCE; }

    public String generateToken(Long userId, String username, String role) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(userId, username, role,
                Instant.now().plusSeconds(TTL_SECONDS)));
        log.debug("Token generated for user={}", username);
        return token;
    }

    public Optional<TokenInfo> validate(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null) return Optional.empty();
        if (Instant.now().isAfter(info.expiration())) {
            tokens.remove(token);
            log.debug("Token expired for user={}", info.username());
            return Optional.empty();
        }
        return Optional.of(info);
    }

    public void invalidate(String token) {
        tokens.remove(token);
    }

    public record TokenInfo(Long userId, String username, String role, Instant expiration) {}

    public static int getTtlSeconds() { return TTL_SECONDS; }
}