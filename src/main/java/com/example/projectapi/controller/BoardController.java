package com.example.projectapi.controller;

import com.example.projectapi.model.Board;
import com.example.projectapi.model.Project;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.BoardRepository;
import com.example.projectapi.service.BoardService;
import com.example.projectapi.service.ProjectService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;
    private final ProjectService projectService;
    private final UserService userService;

    public BoardController(BoardService boardService, ProjectService projectService, UserService userService) {
        this.boardService = boardService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Board>> getBoardByProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(
                boardService.findAllByProjectId(
                        projectService.findById(projectId)
                                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"))
                                .getId()
                )

        );
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<Board> create(@PathVariable Integer projectId, @RequestBody Board board, Authentication authentication) {
        Project project =  projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(projectId, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        board.setProject(project);
        Board created = boardService.create(board);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable Integer id, @RequestBody Board board, Authentication authentication) {
        Board existing = boardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(existing.getProject().getId(), user.getId())) {
            return ResponseEntity.status(403).build();
        }

        Board updated = boardService.update(id, board);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Board> delete(@PathVariable Integer id, Authentication authentication) {
        Board board = boardService.findById(id)
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));

        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if(!projectService.esLider(board.getProject().getId(), user.getId())) {
            return ResponseEntity.status(403).build();
        }

        boardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
