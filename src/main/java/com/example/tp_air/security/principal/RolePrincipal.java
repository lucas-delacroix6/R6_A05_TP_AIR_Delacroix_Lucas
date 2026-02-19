package com.example.tp_air.security.principal;

import java.security.Principal;
import java.util.Objects;

public class RolePrincipal implements Principal {
    private final String role;

    public RolePrincipal(String role) {
        this.role = role;
    }

    @Override
    public String getName() { return role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePrincipal)) return false;
        return Objects.equals(role, ((RolePrincipal) o).role);
    }

    @Override
    public int hashCode() { return Objects.hash(role); }

    @Override
    public String toString() { return "RolePrincipal{role=" + role + "}"; }
}