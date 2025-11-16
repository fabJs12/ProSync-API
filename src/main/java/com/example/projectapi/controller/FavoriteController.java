package com.example.projectapi.controller;

import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.service.FavoriteService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoriteController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public FavoriteController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{taskId}/favorito")
    public ResponseEntity<Void> addFavorite(@PathVariable Integer taskId, Authentication auth) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        favoriteService.addFavorite(user.getId(), taskId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{taskId}/favorito")
    public ResponseEntity<Void> removeFavorite(@PathVariable Integer taskId, Authentication auth) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        favoriteService.removeFavorite(user.getId(), taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Task>> getFavorites(Authentication auth) {
        User user = userService.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(favoriteService.listFavoritesByUser(user.getId()));
    }
}
