package com.example.projectapi.controller;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.User;
import com.example.projectapi.service.ProjectService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/proyectos")
    public List<Project> getProyectosUsuario(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return projectService.getProyectosUsuario(user.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(@PathVariable Integer id) {
        return projectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project project) {
        Project created = projectService.create(project);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Integer id, @RequestBody Project project, @RequestParam Integer userId) {
        if (!projectService.esLider(id, userId)) {
            return ResponseEntity.status(403).build();
        }

        Project updated = projectService.update(id, project);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if(projectService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
