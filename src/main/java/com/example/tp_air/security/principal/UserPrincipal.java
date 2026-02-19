package com.example.tp_air.security.principal;

import java.security.Principal;
import java.util.Objects;

public class UserPrincipal implements Principal {
    private final String username;
    private final Long userId;

    public UserPrincipal(String username, Long userId) {
        this.username = username;
        this.userId = userId;
    }

    @Override
    public String getName() { return username; }

    public Long getUserId() { return userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() { return Objects.hash(username); }

    @Override
    public String toString() { return "UserPrincipal{username=" + username + ", userId=" + userId + "}"; }
}