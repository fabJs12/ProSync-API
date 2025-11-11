package com.example.projectapi.controller;

import com.example.projectapi.model.Integration;
import com.example.projectapi.service.IntegrationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
public class IntegrationController {
    private final IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    public List<Integration> findAll() {
        return integrationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Integration> findById(@PathVariable Integer id) {
        return integrationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Integration> findByUserId(@PathVariable Integer userId) {
        return integrationService.findByUserId(userId);
    }

    @GetMapping("/user/{userId}/service/{nombreServicio}")
    public ResponseEntity<Integration> findByUserIdAndNombreServicio(
            @PathVariable Integer userId,
            @PathVariable String nombreServicio) {
        return integrationService.findByUserIdAndNombreServicio(userId, nombreServicio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{nombreServicio}")
    public List<Integration> findByNombreServicio(@PathVariable String nombreServicio) {
        return integrationService.findByNombreServicio(nombreServicio);
    }

    @PostMapping
    public ResponseEntity<Integration> create(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String nombreServicio = (String) request.get("nombreServicio");
            JsonNode detalles = (JsonNode) request.get("detalles");

            if (userId == null || nombreServicio == null || nombreServicio.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Integration created = integrationService.create(userId, nombreServicio, detalles);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integration> update(@PathVariable Integer id,
                                              @RequestBody Map<String, Object> request) {
        try {
            String nombreServicio = (String) request.get("nombreServicio");
            JsonNode detalles = (JsonNode) request.get("detalles");

            Integration updated = integrationService.update(id, nombreServicio, detalles);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            integrationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        try {
            integrationService.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}