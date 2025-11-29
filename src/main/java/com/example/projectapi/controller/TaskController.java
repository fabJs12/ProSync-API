package com.example.projectapi.controller;

import com.example.projectapi.dto.CreateTaskDTO;
import com.example.projectapi.dto.TaskDTO;
import com.example.projectapi.model.Board;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.service.BoardService;
import com.example.projectapi.service.ProjectService;
import com.example.projectapi.service.TaskService;
import com.example.projectapi.service.UserService;
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
    private  final UserService userService;

    public TaskController(TaskService taskService, BoardService boardService, ProjectService projectService, UserService userService) {
        this.taskService = taskService;
        this.boardService = boardService;
        this.projectService = projectService;
        this.userService = userService;
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

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> update(@PathVariable Integer id, @RequestBody Task updatedTask, Authentication authentication) {
        Task task = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrado"));

        Integer projectId = task.getBoard().getProject().getId();

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean esLider = projectService.esLider(projectId, user.getId());

        boolean esResponsable = task.getResponsable()!= null && task.getResponsable().getId().equals(user.getId());

        if(!esLider && !esResponsable) {
            return ResponseEntity.status(403).build();
        }

        TaskDTO updated = taskService.updateDTO(id, updatedTask);

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