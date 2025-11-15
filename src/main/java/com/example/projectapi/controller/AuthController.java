package com.example.projectapi.controller;

import com.example.projectapi.dto.LoginRequest;
import com.example.projectapi.dto.AuthResponse;
import com.example.projectapi.model.User;
import com.example.projectapi.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService uds, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = uds;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginData.getUsername(),
                            loginData.getPassword()
                    )
            );

            String token = jwtUtil.generateToken(loginData.getUsername());

            return ResponseEntity.ok().body(token);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

    }
}
