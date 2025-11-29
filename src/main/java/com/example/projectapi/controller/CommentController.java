package com.example.projectapi.controller;

import com.example.projectapi.model.Comment;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.service.CommentService;
import com.example.projectapi.service.NotificationService;
import com.example.projectapi.service.TaskService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping
    public List<Comment> findAll() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> findById(@PathVariable Integer id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public List<Comment> findByTaskId(@PathVariable Integer taskId) {
        return commentService.findByTaskId(taskId);
    }

    @GetMapping("/user/{userId}")
    public List<Comment> findByUserId(@PathVariable Integer userId) {
        return commentService.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Comment> create(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            Integer taskId = (Integer) request.get("taskId");
            String contenido = (String) request.get("contenido");

            if (taskId == null || contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Obtener el usuario autenticado
            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Comment created = commentService.create(taskId, user.getId(), contenido);

            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> update(@PathVariable Integer id,
                                          @RequestBody Map<String, String> request) {
        try {
            String contenido = request.get("contenido");
            if (contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Comment updated = commentService.update(id, contenido);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            commentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}