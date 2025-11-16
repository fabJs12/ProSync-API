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
        return estadoRepository.save(estado);
    }
}
