package com.example.projectapi.controller;

import com.example.projectapi.model.Notification;
import com.example.projectapi.model.User;
import com.example.projectapi.service.NotificationService;
import com.example.projectapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    /**
     * Obtener notificaciones del usuario autenticado con paginación
     */
    @GetMapping
    public ResponseEntity<Page<Notification>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean leida,
            Authentication authentication) {
        
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(page, size);
        
        Page<Notification> notifications;
        if (leida != null) {
            notifications = notificationService.findByUserIdAndLeida(user.getId(), leida, pageable);
        } else {
            notifications = notificationService.findByUserId(user.getId(), pageable);
        }
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtener contador de notificaciones no leídas
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long count = notificationService.countUnread(user.getId());
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener una notificación específica (solo si es del usuario autenticado)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Integer id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return notificationService.findById(id)
                .map(notification -> {
                    if (!notification.getUser().getId().equals(user.getId())) {
                        return ResponseEntity.status(403).<Notification>build();
                    }
                    return ResponseEntity.ok(notification);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marcar una notificación como leída
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Integer id, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Notification updated = notificationService.markAsRead(id, user.getId());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permiso")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Marcar una notificación como no leída
     */
    @PatchMapping("/{id}/unread")
    public ResponseEntity<Notification> markAsUnread(@PathVariable Integer id, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Notification updated = notificationService.markAsUnread(id, user.getId());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permiso")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Marcar todas las notificaciones como leídas
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        int count = notificationService.markAllAsRead(user.getId());
        
        Map<String, Integer> response = new HashMap<>();
        response.put("updated", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una notificación (solo si es del usuario autenticado)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            notificationService.delete(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("permiso")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar todas las notificaciones del usuario autenticado
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAll(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            notificationService.deleteByUserId(user.getId(), user.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}