package com.example.projectapi.controller;

import com.example.projectapi.model.Comment;
import com.example.projectapi.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
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
    public ResponseEntity<Comment> create(@RequestBody Map<String, Object> request) {
        try {
            Integer taskId = (Integer) request.get("taskId");
            Integer userId = (Integer) request.get("userId");
            String contenido = (String) request.get("contenido");

            if (taskId == null || userId == null || contenido == null || contenido.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Comment created = commentService.create(taskId, userId, contenido);
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