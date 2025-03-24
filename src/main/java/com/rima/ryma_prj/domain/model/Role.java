package com.rima.ryma_prj.domain.model;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_SUPER_ADMIN;

    public String getName() {
        return name(); // Retourne "ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"
    }

}

