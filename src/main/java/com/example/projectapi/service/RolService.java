package com.example.projectapi.service;

import com.example.projectapi.model.Rol;
import com.example.projectapi.repository.RolRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public Optional<Rol> findById(Integer id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> findByRol(String rol) {
        return rolRepository.findByRol(rol);
    }

    public Rol create(Rol rol) {
        if (rolRepository.findByRol(rol.getRol()).isPresent()) {
            throw new RuntimeException("El rol ya existe");
        }
        return rolRepository.save(rol);
    }

    public Rol update(Integer id, Rol updatedRol) {
        return rolRepository.findById(id)
                .map(existing -> {
                    if (!existing.getRol().equals(updatedRol.getRol())
                            && rolRepository.findByRol(updatedRol.getRol()).isPresent()) {
                        throw new RuntimeException("El rol ya existe");
                    }
                    existing.setRol(updatedRol.getRol());
                    return rolRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    public void delete(Integer id) {
        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado");
        }
        rolRepository.deleteById(id);
    }
}