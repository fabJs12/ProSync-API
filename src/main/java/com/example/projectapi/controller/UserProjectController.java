package com.example.projectapi.controller;

import com.example.projectapi.model.UserProject;
import com.example.projectapi.model.UserProjectId;
import com.example.projectapi.service.UserProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;

    public UserProjectController(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    @GetMapping
    public List<UserProject> findAll() {
        return userProjectService.findAll();
    }

    @GetMapping("/{userId}/{projectId}")
    public ResponseEntity<UserProject> findById(@PathVariable Integer userId,
                                                @PathVariable Integer projectId) {
        UserProjectId id = new UserProjectId(userId, projectId);
        return userProjectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<UserProject> findByUserId(@PathVariable Integer userId) {
        return userProjectService.findByUserId(userId);
    }

    @GetMapping("/project/{projectId}")
    public List<UserProject> findByProjectId(@PathVariable Integer projectId) {
        return userProjectService.findByProjectId(projectId);
    }

    @GetMapping("/rol/{rolId}")
    public List<UserProject> findByRolId(@PathVariable Integer rolId) {
        return userProjectService.findByRolId(rolId);
    }

    @PostMapping
    public ResponseEntity<UserProject> create(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");
            Integer projectId = request.get("projectId");
            Integer rolId = request.get("rolId");

            if (userId == null || projectId == null || rolId == null) {
                return ResponseEntity.badRequest().build();
            }

            UserProject created = userProjectService.create(userId, projectId, rolId);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}/{projectId}")
    public ResponseEntity<UserProject> update(@PathVariable Integer userId,
                                              @PathVariable Integer projectId,
                                              @RequestBody Map<String, Integer> request) {
        try {
            Integer newRolId = request.get("rolId");
            if (newRolId == null) {
                return ResponseEntity.badRequest().build();
            }

            UserProject updated = userProjectService.update(userId, projectId, newRolId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{userId}/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable Integer userId,
                                       @PathVariable Integer projectId) {
        try {
            userProjectService.delete(userId, projectId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}