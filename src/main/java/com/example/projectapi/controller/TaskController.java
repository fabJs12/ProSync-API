package com.example.projectapi.controller;

import com.example.projectapi.dto.CreateTaskDTO;
import com.example.projectapi.dto.TaskDTO;
import com.example.projectapi.model.Board;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TaskController {

    private final TaskService taskService;
    private final BoardService boardService;
    private final ProjectService projectService;
    private final UserService userService;
    private final NotificationService notificationService;

    public TaskController(TaskService taskService, 
                         BoardService boardService, 
                         ProjectService projectService, 
                         UserService userService,
                         NotificationService notificationService) {
        this.taskService = taskService;
        this.boardService = boardService;
        this.projectService = projectService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<TaskDTO>> getTasksByBoard(@PathVariable Integer boardId) {
        return ResponseEntity.ok(taskService.findByBoardDTO(boardId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        return taskService.findByIdDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<TaskDTO>> getTasksByEstado(@PathVariable Integer estadoId) {
        return ResponseEntity.ok(taskService.findByEstadoDTO(estadoId));
    }

    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<TaskDTO>> getTasksByResponsable(@PathVariable Integer responsableId) {
        return ResponseEntity.ok(taskService.findByResponsableDTO(responsableId));
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<TaskDTO>> getTasksByUsuario(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(taskService.findByUsuarioDTO(user.getId()));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> create(
            @RequestBody CreateTaskDTO taskDTO,
            Authentication authentication
    ) {
        Board board = boardService.findById(taskDTO.getBoardId())
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(board.getProject().getId(), user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());

        TaskDTO created = taskService.createDTO(
                task,
                taskDTO.getBoardId(),
                taskDTO.getEstadoId(),
                taskDTO.getResponsableId()
        );

        // Notificar al responsable si existe
        if (taskDTO.getResponsableId() != null && !taskDTO.getResponsableId().equals(user.getId())) {
            notificationService.notifyTaskAssigned(
                taskDTO.getResponsableId(),
                created.getId(),
                created.getTitle(),
                user.getUsername()
            );
        }

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Integer id, @RequestBody Task updatedTask, Authentication authentication) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        Integer projectId = task.getBoard().getProject().getId();

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esLider = projectService.esLider(projectId, user.getId());
        boolean esResponsable = task.getResponsable() != null && task.getResponsable().getId().equals(user.getId());

        if(!esLider && !esResponsable) {
            return ResponseEntity.status(403).build();
        }

        // Guardar datos anteriores para comparar
        Integer oldResponsableId = task.getResponsable() != null ? task.getResponsable().getId() : null;
        Integer newResponsableId = updatedTask.getResponsable() != null ? updatedTask.getResponsable().getId() : null;

        TaskDTO updated = taskService.updateDTO(id, updatedTask);

        // Notificar si cambió el responsable
        if (newResponsableId != null && !newResponsableId.equals(oldResponsableId) && !newResponsableId.equals(user.getId())) {
            notificationService.notifyTaskAssigned(
                newResponsableId,
                updated.getId(),
                updated.getTitle(),
                user.getUsername()
            );
        }

        // Notificar al responsable anterior si existe y es diferente del que hace la actualización
        if (oldResponsableId != null && !oldResponsableId.equals(user.getId()) && !oldResponsableId.equals(newResponsableId)) {
            notificationService.notifyTaskUpdated(
                oldResponsableId,
                updated.getId(),
                updated.getTitle(),
                user.getUsername()
            );
        }

        // Si el responsable actual no cambió pero se actualizó la tarea, notificar
        if (newResponsableId != null && newResponsableId.equals(oldResponsableId) && !newResponsableId.equals(user.getId())) {
            notificationService.notifyTaskUpdated(
                newResponsableId,
                updated.getId(),
                updated.getTitle(),
                user.getUsername()
            );
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication authentication) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        Integer projectId = task.getBoard().getProject().getId();

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(projectId, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        taskService.delete(id);

        return ResponseEntity.noContent().build();
    }
}