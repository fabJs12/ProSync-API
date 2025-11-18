package com.example.projectapi.controller;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import com.example.projectapi.model.User;
import com.example.projectapi.service.ProjectService;
import com.example.projectapi.service.RolService;
import com.example.projectapi.service.UserProjectService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final UserService userService;
    private final RolService rolService;
    private final UserProjectService userProjectService;

    public ProjectController(ProjectService projectService, UserService userService, RolService rolService,  UserProjectService userProjectService) {
        this.projectService = projectService;
        this.userService = userService;
        this.rolService = rolService;
        this.userProjectService = userProjectService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Project>> getProyectosUsuario(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Project> proyectos = projectService.getProyectosUsuario(user.getId());
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(@PathVariable Integer id, Authentication authentication) {
        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<Project> projectOpt = projectService.findById(id);

        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!userProjectService.usuarioTieneRelacion(user.getId(), id)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        return ResponseEntity.ok(projectOpt.get());
    }

    @PostMapping("/crear")
    public ResponseEntity<Project> create(@RequestBody Project project, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rolLider = rolService.findByRol("Lider")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Project created = projectService.create(project, user, rolLider);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Integer id, @RequestBody Project project, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        if (!projectService.esLider(id, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Project updated = projectService.update(id, project);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(id, user.getId())) {
            return ResponseEntity.status(403).build();
        }
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
