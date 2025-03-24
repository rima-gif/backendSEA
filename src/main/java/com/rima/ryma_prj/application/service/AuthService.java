package com.rima.ryma_prj.application.service;

import com.rima.ryma_prj.domain.model.Role;
import com.rima.ryma_prj.domain.model.User;
import com.rima.ryma_prj.domain.repository.UserRepository;
import com.rima.ryma_prj.infrastructure.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<Map<String, Object>> signup(String username, String email, String password) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(email).isPresent()) {
            response.put("message", "Email already in use");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = User.createNewUser(
                username,
                email,
                passwordEncoder.encode(password),
                Collections.singleton(Role.ROLE_USER)
        );

        userRepository.save(user);

        response.put("message", "User registered successfully!");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<Map<String, Object>> signin(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Attempting sign-in with email: " + email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            System.out.println("User not found.");
            response.put("error", "Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Incorrect password.");
            response.put("error", "Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = jwtUtil.generateToken(user);

        response.put("token", token);
        response.put("id", user.getId());
        response.put("name", user.getUsername());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }





    public ResponseEntity<Map<String, Object>> forgotPassword(String email) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            response.put("error", "Email non enregistré");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOptional.get();
        String resetToken = jwtUtil.generatePasswordResetToken(email);
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        sendResetPasswordEmail(email, resetLink);

        response.put("message", "Email envoyé avec succès !");
        return ResponseEntity.ok(response);
    }


    private void sendResetPasswordEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("Cliquez sur le lien suivant pour réinitialiser votre mot de passe : " + resetLink);
        mailSender.send(message);
    }

    public ResponseEntity<Map<String, Object>> resetPasswordWithToken(String token, String newPassword, String confirmPassword) {
        Map<String, Object> response = new HashMap<>();

        String email = jwtUtil.validatePasswordResetToken(token);
        if (email == null) {
            response.put("error", "Token invalide ou expiré");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            response.put("error", "Utilisateur non trouvé");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (!newPassword.equals(confirmPassword)) {
            response.put("error", "Les nouveaux mots de passe ne correspondent pas");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        response.put("message", "Mot de passe mis à jour avec succès !");
        return ResponseEntity.ok(response);
    }


}
