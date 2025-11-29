package com.example.projectapi.controller;

import com.example.projectapi.dto.CreateUserProjectDTO;
import com.example.projectapi.model.User;
import com.example.projectapi.model.UserProject;
import com.example.projectapi.model.UserProjectId;
import com.example.projectapi.service.NotificationService;
import com.example.projectapi.service.ProjectService;
import com.example.projectapi.service.UserProjectService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {
    private final UserProjectService userProjectService;
    private final UserService userService;
    private final ProjectService projectService;
    private final NotificationService notificationService;

    public UserProjectController(UserProjectService userProjectService, 
                                 UserService userService,
                                 ProjectService projectService,
                                 NotificationService notificationService) {
        this.userProjectService = userProjectService;
        this.userService = userService;
        this.projectService = projectService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<UserProject>> findAll(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<UserProject> userProjects = userProjectService.findByUserId(user.getId());
        return ResponseEntity.ok(userProjects);
    }

    @GetMapping("/{userId}/{projectId}")
    public ResponseEntity<UserProject> findById(@PathVariable Integer userId,
                                                @PathVariable Integer projectId,
                                                Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isOwner = currentUser.getId().equals(userId);
        boolean isLeader = projectService.esLider(projectId, currentUser.getId());

        if (!isOwner && !isLeader) {
            return ResponseEntity.status(403).build();
        }

        UserProjectId id = new UserProjectId(userId, projectId);
        return userProjectService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProject>> findByUserId(@PathVariable Integer userId,
                                                          Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (currentUser.getId().equals(userId)) {
            return ResponseEntity.ok(userProjectService.findByUserId(userId));
        }

        List<UserProject> currentUserProjects = userProjectService.findByUserId(currentUser.getId());
        List<UserProject> targetUserProjects = userProjectService.findByUserId(userId);

        boolean sharesProject = currentUserProjects.stream()
                .anyMatch(cup -> targetUserProjects.stream()
                        .anyMatch(tup -> cup.getProyecto().getId().equals(tup.getProyecto().getId())));

        if (!sharesProject) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(targetUserProjects);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<UserProject>> findByProjectId(@PathVariable Integer projectId,
                                                             Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!userProjectService.usuarioTieneRelacion(user.getId(), projectId)) {
            return ResponseEntity.status(403).build();
        }

        List<UserProject> members = userProjectService.findByProjectId(projectId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UserProject>> findByRolId(@PathVariable Integer rolId) {
        List<UserProject> userProjects = userProjectService.findByRolId(rolId);
        return ResponseEntity.ok(userProjects);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserProjectDTO dto, 
                                   Authentication authentication) {
        try {
            if (dto.getUserId() == null || dto.getProjectId() == null || dto.getRolId() == null) {
                return ResponseEntity.badRequest()
                        .body("userId, projectId y rolId son requeridos");
            }

            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!projectService.esLider(dto.getProjectId(), currentUser.getId())) {
                return ResponseEntity.status(403)
                        .body("Solo el líder del proyecto puede agregar usuarios");
            }

            UserProject created = userProjectService.create(
                    dto.getUserId(),
                    dto.getProjectId(),
                    dto.getRolId()
            );

            // Crear notificación usando el método mejorado
            notificationService.notifyProjectAdded(
                dto.getUserId(),
                created.getProyecto().getName(),
                currentUser.getUsername(),
                created.getRol().getRol()
            );

            return ResponseEntity.status(201).body(created);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{userId}/{projectId}")
    public ResponseEntity<?> update(@PathVariable Integer userId,
                                   @PathVariable Integer projectId,
                                   @RequestBody Map<String, Integer> request,
                                   Authentication authentication) {
        try {
            Integer newRolId = request.get("rolId");
            if (newRolId == null) {
                return ResponseEntity.badRequest()
                        .body("rolId es requerido");
            }

            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!projectService.esLider(projectId, currentUser.getId())) {
                return ResponseEntity.status(403)
                        .body("Solo el líder del proyecto puede cambiar roles");
            }

            if (currentUser.getId().equals(userId)) {
                List<UserProject> projectMembers = userProjectService.findByProjectId(projectId);
                long leaderCount = projectMembers.stream()
                        .filter(up -> up.getRol().getRol().equals("Lider"))
                        .count();
                
                if (leaderCount <= 1) {
                    return ResponseEntity.badRequest()
                            .body("No puedes quitarte el rol de líder si eres el único líder del proyecto");
                }
            }

            UserProject updated = userProjectService.update(userId, projectId, newRolId);

            // Crear notificación usando el método mejorado
            notificationService.notifyRoleChanged(
                userId,
                updated.getProyecto().getName(),
                updated.getRol().getRol(),
                currentUser.getUsername()
            );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/{projectId}")
    public ResponseEntity<?> delete(@PathVariable Integer userId,
                                   @PathVariable Integer projectId,
                                   Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!projectService.esLider(projectId, currentUser.getId())) {
                return ResponseEntity.status(403)
                        .body("Solo el líder del proyecto puede remover usuarios");
            }

            if (currentUser.getId().equals(userId)) {
                List<UserProject> projectMembers = userProjectService.findByProjectId(projectId);
                long leaderCount = projectMembers.stream()
                        .filter(up -> up.getRol().getRol().equals("Lider"))
                        .count();
                
                if (leaderCount <= 1) {
                    return ResponseEntity.badRequest()
                            .body("No puedes abandonar el proyecto si eres el único líder. Asigna otro líder primero.");
                }
            }

            UserProject userProject = userProjectService.findById(new UserProjectId(userId, projectId))
                    .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
            String projectName = userProject.getProyecto().getName();

            userProjectService.delete(userId, projectId);

            // Crear notificación usando el método mejorado
            notificationService.notifyProjectRemoved(
                userId,
                projectName,
                currentUser.getUsername()
            );

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}