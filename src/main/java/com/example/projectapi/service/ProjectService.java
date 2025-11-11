package com.example.projectapi.service;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import com.example.projectapi.repository.ProjectRepository;
import com.example.projectapi.repository.RolRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final RolRepository rolRepository;

    public ProjectService(ProjectRepository projectRepository, RolRepository rolRepository) {
        this.projectRepository = projectRepository;
        this.rolRepository = rolRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // Verifica si el usuario es líder de un proyecto
    public boolean esLider(Integer projectId, Integer userId) {
        Rol rolLider = rolRepository.findByRol("Lider").orElseThrow(() -> new RuntimeException("Rol Lider no encontrado"));;

        List<Project> proyectosLider = projectRepository
                .findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolLider);

        // Devuelve true si la lista contiene el proyecto indicado
        return proyectosLider.stream().anyMatch(p -> p.getId().equals(projectId));
    }

    public List<Project> getProyectosUsuario(Integer userId) {
        return projectRepository.findByUsuariosAsociadosUsuarioId(userId);
    }

    public List<Project> getProyectosUsuarioEsLider(Integer userId, Rol rolLider) { return projectRepository.findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolLider); }

    public List<Project> getProyectosUsuarioEsMiembro(Integer userId, Rol rolMiembro) { return projectRepository.findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolMiembro); }

    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    public Project update(Integer id, Project upProject) {
        return projectRepository.findById(id)
                .map(existing -> {
                    existing.setName(upProject.getName());
                    existing.setDescription(upProject.getDescription());
                    return projectRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }

    public void delete(Integer id) {
        projectRepository.deleteById(id);
    }
}
