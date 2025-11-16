package com.example.projectapi.controller;

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
    public ResponseEntity<List<Task>> getTasksByBoard(@PathVariable Integer boardId) {
        return ResponseEntity.ok(taskService.findByBoard(boardId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<Task>> getTasksByEstado(@PathVariable Integer estadoId) {
        return ResponseEntity.ok(taskService.findByEstado(estadoId));
    }

    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<Task>> getTasksByResponsable(@PathVariable Integer responsableId) {
        return ResponseEntity.ok(taskService.findByResponsable(responsableId));
    }

    @PostMapping("/board/{boardId}")
    public ResponseEntity<Task> create(
            @PathVariable Integer boardId,
            @RequestParam Integer estadoId,
            @RequestParam(required = false) Integer responsableId,
            @RequestBody Task task,
            Authentication authentication
    ) {
        Board board = boardService.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));

        Integer projectId = board.getProject().getId();

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(projectId, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Task created = taskService.create(task, boardId, estadoId, responsableId);

        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Integer id, @RequestBody Task updatedTask, Authentication authentication) {
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

        Task updated = taskService.update(id, updatedTask);

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
