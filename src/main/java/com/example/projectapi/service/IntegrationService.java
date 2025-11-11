package com.example.projectapi.service;

import com.example.projectapi.model.Integration;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.IntegrationRepository;
import com.example.projectapi.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class IntegrationService {
    private final IntegrationRepository integrationRepository;
    private final UserRepository userRepository;

    public IntegrationService(IntegrationRepository integrationRepository,
                              UserRepository userRepository) {
        this.integrationRepository = integrationRepository;
        this.userRepository = userRepository;
    }

    public List<Integration> findAll() {
        return integrationRepository.findAll();
    }

    public Optional<Integration> findById(Integer id) {
        return integrationRepository.findById(id);
    }

    public List<Integration> findByUserId(Integer userId) {
        return integrationRepository.findByUserId(userId);
    }

    public Optional<Integration> findByUserIdAndServiceName(Integer userId, String serviceName) {
        return integrationRepository.findByUserIdAndServiceName(userId, serviceName);
    }

    public List<Integration> findByServiceName(String serviceName) {
        return integrationRepository.findByServiceName(serviceName);
    }

    public Integration create(Integer userId, String serviceName, JsonNode details) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya existe una integración para este usuario y servicio
        Optional<Integration> existing = integrationRepository
                .findByUserIdAndServiceName(userId, serviceName);
        if (existing.isPresent()) {
            throw new RuntimeException("Ya existe una integración para este usuario y servicio");
        }

        Integration integration = new Integration();
        integration.setUser(user);
        integration.setServiceName(serviceName);
        integration.setDetails(details);

        return integrationRepository.save(integration);
    }

    public Integration update(Integer id, String serviceName, JsonNode details) {
        return integrationRepository.findById(id)
                .map(existing -> {
                    if (serviceName != null && !serviceName.trim().isEmpty()) {
                        existing.setServiceName(serviceName);
                    }
                    if (details != null) {
                        existing.setDetails(details);
                    }
                    return integrationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Integración no encontrada"));
    }

    public void delete(Integer id) {
        if (!integrationRepository.existsById(id)) {
            throw new RuntimeException("Integración no encontrada");
        }
        integrationRepository.deleteById(id);
    }

    public void deleteByUserId(Integer userId) {
        List<Integration> integrations = integrationRepository.findByUserId(userId);
        integrationRepository.deleteAll(integrations);
    }
}