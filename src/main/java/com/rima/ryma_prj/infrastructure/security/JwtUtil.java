package com.rima.ryma_prj.infrastructure.security;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.rima.ryma_prj.domain.model.User;
import com.rima.ryma_prj.domain.model.Role; // Assurez-vous d'avoir importé le modèle de rôle

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationTime;

    @Value("${jwt.reset-password.expiration}")
    private long resetPasswordExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expiration;
    }

    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName) // S'assurer que getName() retourne bien une String
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(user.getEmail()) // Stocke l'email comme identifiant principal
                .claim("roles", roles) // Ajoute les rôles comme une liste
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Set<Role> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Récupère la liste des rôles sous forme de String
        List<String> roles = claims.get("roles", List.class);

        // Si roles est null, retourne un Set vide
        if (roles == null) {
            return Collections.emptySet();
        }

        // Convertit les rôles en enum Role
        return roles.stream()
                .map(roleName -> {
                    try {
                        // Assurez-vous que le rôle est préfixé par "ROLE_" si nécessaire
                        if (!roleName.startsWith("ROLE_")) {
                            roleName = "ROLE_" + roleName;
                        }
                        return Role.valueOf(roleName); // Convertit en enum Role
                    } catch (IllegalArgumentException e) {
                        // Log l'erreur et ignore les rôles invalides
                        Logger log = null;
                        log.warn("Rôle invalide dans le token JWT : {}", roleName);
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Filtre les rôles invalides
                .collect(Collectors.toSet());
    }


    public String generatePasswordResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + resetPasswordExpiration))
                .signWith(key)
                .compact();
    }


    public String validatePasswordResetToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
