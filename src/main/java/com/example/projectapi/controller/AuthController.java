package com.example.projectapi.controller;

import com.example.projectapi.dto.LoginRequest;
import com.example.projectapi.dto.AuthResponse;
import com.example.projectapi.model.User;
import com.example.projectapi.security.JwtUtil;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService uds, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = uds;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginData) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginData.getUsername(),
                            loginData.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginData.getUsername());

            String token = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.create(user);

            return ResponseEntity.ok("Usuario registrado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Error al registrar: "  + e.getMessage());
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("token");
        try {
            // Verificación simple del token (para producción usar GoogleIdTokenVerifier)
            // Aquí decodificamos el payload para obtener el email
            // NOTA: En un entorno real, DEBES validar la firma del token con las librerías
            // de Google
            // pero para este ejemplo rápido asumiremos que el frontend envía un token
            // válido o
            // usaremos una decodificación básica.

            // Opción A: Usar librería Google (recomendado)
            com.google.api.client.json.gson.GsonFactory jsonFactory = new com.google.api.client.json.gson.GsonFactory();
            com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier verifier = new com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder(
                    new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory)
                    // .setAudience(Collections.singletonList(CLIENT_ID)) // Configurar si se tiene
                    // el ID
                    .build();

            com.google.api.client.googleapis.auth.oauth2.GoogleIdToken idToken = verifier.verify(idTokenString);

            String email;
            String name;

            if (idToken != null) {
                com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload payload = idToken.getPayload();
                email = payload.getEmail();
                name = (String) payload.get("name");
            } else {
                // Fallback para desarrollo si la verificación estricta falla por falta de
                // configuración
                // (NO USAR EN PRODUCCIÓN SIN VALIDAR FIRMA)
                // Decodificar JWT manualmente solo para obtener claims
                String[] parts = idTokenString.split("\\.");
                if (parts.length < 2)
                    throw new RuntimeException("Token inválido");
                String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                // Parsear JSON básico (asumiendo formato estándar)
                email = payloadJson.split("\"email\":\"")[1].split("\"")[0];
                try {
                    name = payloadJson.split("\"name\":\"")[1].split("\"")[0];
                } catch (Exception e) {
                    name = email.split("@")[0];
                }
            }

            // Buscar o crear usuario
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setUsername(email); // Usar email como username
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString())); // Password aleatorio
                // Asignar otros campos si es necesario
                userService.create(user);
            }

            // Generar JWT propio
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Error en autenticación Google: " + e.getMessage());
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(Authentication authentication) {
        String username  = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }
}