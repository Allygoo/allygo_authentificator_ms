package com.allygo.allygoauthenticatorms.Controller;

import com.allygo.allygoauthenticatorms.Record.NewUser;
import com.allygo.allygoauthenticatorms.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Registrar un nuevo usuario (ID autogenerado por Firestore)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody NewUser newUser) {
        try {
            logger.info("Register request received: {}", newUser);
            String result = userService.saveUser(newUser);
            logger.info("User successfully saved: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error registering user", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Obtener usuario por ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            logger.info("Get user by ID request: {}", id);
            NewUser user = userService.getUserById(id);
            if (user != null) {
                logger.info("User found: {}", user);
                return ResponseEntity.ok(user);
            } else {
                logger.warn("User not found with ID: {}", id);
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            logger.error("Error getting user by ID", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Obtener usuario por email (enviado en el body)
    @PostMapping("/user/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            logger.info("Get user by email request: {}", email);

            if (email == null || email.isEmpty()) {
                logger.warn("Email not provided in request body");
                return ResponseEntity.badRequest().body("Email is required");
            }

            NewUser user = userService.getUserByEmail(email);
            if (user != null) {
                logger.info("User found by email: {}", user);
                return ResponseEntity.ok(user);
            } else {
                logger.warn("User not found with email: {}", email);
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            logger.error("Error getting user by email", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
