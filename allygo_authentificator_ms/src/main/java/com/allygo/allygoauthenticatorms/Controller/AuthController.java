package com.allygo.allygoauthenticatorms.Controller;

import com.allygo.allygoauthenticatorms.JwtUtil;
import com.allygo.allygoauthenticatorms.Record.NewUser;
import com.allygo.allygoauthenticatorms.Services.LoginService;
import com.allygo.allygoauthenticatorms.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registro de usuario en Firestore con password encriptada
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody NewUser user) {
        try {
            String result = userService.saveUser(user);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error registrando usuario: " + e.getMessage());
        }
    }

    /**
     * Login de usuario con validación de contraseña
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginService loginRequest) {
        try {
            NewUser user = userService.getUserByEmail(loginRequest.getEmail());
            if (user == null) {
                return ResponseEntity.status(401).body("Usuario no encontrado");
            }

            boolean passwordOk = userService.checkPassword(loginRequest.getPassword(), user.getPassword());
            if (!passwordOk) {
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            }

            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error en login: " + e.getMessage());
        }
    }

    /**
     * Validar un token JWT
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token requerido");
        }

        boolean valid = jwtUtil.validateToken(token);
        if (!valid) {
            return ResponseEntity.status(401).body(Map.of("valid", false, "message", "Token inválido"));
        }

        String email = jwtUtil.extractEmail(token);
        return ResponseEntity.ok(Map.of("valid", true, "email", email));
    }

    /**
     * Obtener usuario por ID
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            NewUser user = userService.getUserById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(500).body("Error obteniendo usuario: " + e.getMessage());
        }
    }

    /**
     * Obtener usuario por email
     */
    @PostMapping("/user/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email es requerido");
            }

            NewUser user = userService.getUserByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo usuario: " + e.getMessage());
        }
    }
}
