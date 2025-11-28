package com.example.projectapi.service;

import com.example.projectapi.model.Estado;
import com.example.projectapi.repository.EstadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoService {
    private final EstadoRepository estadoRepository;

    public EstadoService(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    public List<Estado> findAll() {
        return estadoRepository.findAll();
    }

    public Optional<Estado> findById(Integer id) {
        return estadoRepository.findById(id);
    }

    public Estado create(Estado estado) {
        if (estado.getEstado() == null || estado.getEstado().trim().isEmpty()) {
            throw new RuntimeException("El nombre del estado es requerido");
        }
        return estadoRepository.save(estado);
    }

    public Estado update(Integer id, Estado updatedEstado) {
        return estadoRepository.findById(id)
                .map(existing -> {
                    existing.setEstado(updatedEstado.getEstado());
                    return estadoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }

    public void delete(Integer id) {
        if (!estadoRepository.existsById(id)) {
            throw new RuntimeException("Estado no encontrado");
        }
        estadoRepository.deleteById(id);
    }
}