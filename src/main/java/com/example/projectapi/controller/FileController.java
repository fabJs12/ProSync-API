package com.example.projectapi.controller;

import com.example.projectapi.model.File;
import com.example.projectapi.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public List<File> findAll() {
        return fileService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> findById(@PathVariable Integer id) {
        return fileService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public List<File> findByTaskId(@PathVariable Integer taskId) {
        return fileService.findByTaskId(taskId);
    }

    @PostMapping
    public ResponseEntity<File> create(@RequestBody Map<String, Object> request) {
        try {
            Integer taskId = (Integer) request.get("taskId");
            String archivoUrl = (String) request.get("archivoUrl");

            if (taskId == null || archivoUrl == null || archivoUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            File created = fileService.create(taskId, archivoUrl);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<File> update(@PathVariable Integer id,
                                       @RequestBody Map<String, String> request) {
        try {
            String archivoUrl = request.get("archivoUrl");
            if (archivoUrl == null || archivoUrl.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            File updated = fileService.update(id, archivoUrl);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            fileService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}