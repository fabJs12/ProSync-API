package com.example.projectapi.controller;

import com.example.projectapi.model.TaskFile;
import com.example.projectapi.model.User;
import com.example.projectapi.service.FileService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService,  UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<TaskFile>> findAll() {
        return ResponseEntity.ok(fileService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskFile> findById(@PathVariable Integer id) {
        return fileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskFile>> findByTaskId(@PathVariable Integer taskId) {
        return ResponseEntity.ok(fileService.findByTaskId(taskId));
    }

    @PostMapping("/task/{taskId}")
    public ResponseEntity<TaskFile> upload(@PathVariable Integer taskId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            TaskFile created = fileService.uploadFile(taskId, file);
            return ResponseEntity.status(201).body(created);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Authentication authentication) {
        try {
            TaskFile archivo = fileService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));

            User user = userService.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Integer projectId = archivo.getTask().getBoard().getProject().getId();

            boolean esResponsable = archivo.getTask().getResponsable() != null &&
                    archivo.getTask().getResponsable().getId().equals(user.getId());

            if (!esResponsable) {
                return ResponseEntity.status(403).build();
            }

            fileService.delete(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}