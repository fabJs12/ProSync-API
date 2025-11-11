package com.example.projectapi.controller;

import com.example.projectapi.model.Notification;
import com.example.projectapi.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> findAll() {
        return notificationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Integer id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Notification> findByUserId(@PathVariable Integer userId) {
        return notificationService.findByUserId(userId);
    }

    @GetMapping("/task/{taskId}")
    public List<Notification> findByTaskId(@PathVariable Integer taskId) {
        return notificationService.findByTaskId(taskId);
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            Integer taskId = (Integer) request.get("taskId");
            String mensaje = (String) request.get("mensaje");

            if (userId == null || taskId == null || mensaje == null || mensaje.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Notification created = notificationService.create(userId, taskId, mensaje);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> update(@PathVariable Integer id,
                                               @RequestBody Map<String, String> request) {
        try {
            String mensaje = request.get("mensaje");
            if (mensaje == null || mensaje.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Notification updated = notificationService.update(id, mensaje);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Integer id) {
        try {
            Notification updated = notificationService.markAsRead(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/unread")
    public ResponseEntity<Notification> markAsUnread(@PathVariable Integer id) {
        try {
            Notification updated = notificationService.markAsUnread(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            notificationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        try {
            notificationService.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}