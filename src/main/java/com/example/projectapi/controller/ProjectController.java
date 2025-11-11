package com.example.projectapi.controller;

import com.example.projectapi.model.Project;
import com.example.projectapi.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/usuario/{userId}")
    public List<Project> getProyectosUsuario(@PathVariable Integer userId) {
        return projectService.getProyectosUsuario(userId);
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
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
