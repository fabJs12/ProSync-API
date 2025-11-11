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

    @GetMapping("/user/{userId}/service/{serviceName}")
    public ResponseEntity<Integration> findByUserIdAndServiceName(
            @PathVariable Integer userId,
            @PathVariable String serviceName) {
        return integrationService.findByUserIdAndServiceName(userId, serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceName}")
    public List<Integration> findByServiceName(@PathVariable String serviceName) {
        return integrationService.findByServiceName(serviceName);
    }

    @PostMapping
    public ResponseEntity<Integration> create(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String serviceName = (String) request.get("serviceName");
            JsonNode details = (JsonNode) request.get("details");

            if (userId == null || serviceName == null || serviceName.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Integration created = integrationService.create(userId, serviceName, details);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integration> update(@PathVariable Integer id,
                                              @RequestBody Map<String, Object> request) {
        try {
            String serviceName = (String) request.get("serviceName");
            JsonNode details = (JsonNode) request.get("details");

            Integration updated = integrationService.update(id, serviceName, details);
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