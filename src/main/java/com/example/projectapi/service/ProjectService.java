package com.example.projectapi.service;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import com.example.projectapi.model.User;
import com.example.projectapi.model.UserProject;
import com.example.projectapi.repository.ProjectRepository;
import com.example.projectapi.repository.RolRepository;
import com.example.projectapi.repository.UserProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final RolRepository rolRepository;
    private final UserProjectRepository userProjectRepository;

    public ProjectService(ProjectRepository projectRepository, RolRepository rolRepository, UserProjectRepository userProjectRepository) {
        this.projectRepository = projectRepository;
        this.rolRepository = rolRepository;
        this.userProjectRepository = userProjectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // Verifica si el usuario es líder de un proyecto
    public boolean esLider(Integer projectId, Integer userId) {
        Rol rolLider = rolRepository.findByRol("Lider")
                .orElseThrow(() -> new RuntimeException("Rol Lider no encontrado"));;

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

    public Project create(Project project, User user, Rol rolLider) {
        if (project.getName() == null || project.getName().isEmpty()) {
            throw new RuntimeException("El proyecto debe tener un nombre");
        }

        Project created = projectRepository.save(project);

        UserProject userProject = new UserProject(user, created, rolLider);
        userProjectRepository.save(userProject);

        return created;
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

    @Transactional // si algo no se concreta, se cancela la acción
    public void delete(Integer id) {
        Project project = projectRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        userProjectRepository.deleteAll(userProjectRepository.findByProyectoId(id));

        projectRepository.delete(project);
    }
}
