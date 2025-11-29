package com.example.projectapi.service;

import com.example.projectapi.model.*;
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
    private final UserProjectService userProjectService;

    public ProjectService(ProjectRepository projectRepository, RolRepository rolRepository, UserProjectRepository userProjectRepository, UserProjectService userProjectService) {
        this.projectRepository = projectRepository;
        this.rolRepository = rolRepository;
        this.userProjectRepository = userProjectRepository;
        this.userProjectService = userProjectService;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public boolean esLider(Integer projectId, Integer userId) {
        Rol rolLider = rolRepository.findByRol("Lider")
                .orElseThrow(() -> new RuntimeException("Rol Lider no encontrado"));;

        List<Project> proyectosLider = projectRepository
                .findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolLider);

        return proyectosLider.stream().anyMatch(p -> p.getId().equals(projectId));
    }

    public List<Project> getProyectosUsuario(Integer userId) {
        return projectRepository.findByUsuariosAsociadosUsuarioId(userId);
    }

    public List<Project> getProyectosUsuarioEsLider(Integer userId, Rol rolLider) { 
        return projectRepository.findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolLider); 
    }

    public List<Project> getProyectosUsuarioEsMiembro(Integer userId, Rol rolMiembro) { 
        return projectRepository.findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(userId, rolMiembro); 
    }

    public boolean esMiembro(Integer userId, Integer projectId) {
        return userProjectService.usuarioTieneRelacion(projectId, userId);
    }

    public Optional<Project> findById(Integer id) {
        return projectRepository.findById(id);
    }

    public Project create(Project project, User user, Rol rolLider) {
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new RuntimeException("El proyecto debe tener un nombre");
        }

        Optional<Project> existingProject = projectRepository.findByUsuarioIdAndNombre(user.getId(), project.getName());
        if (existingProject.isPresent()) {
            throw new RuntimeException("Ya tienes un proyecto con este nombre");
        }

        Project created = projectRepository.save(project);

        UserProject userProject = new UserProject(user, created, rolLider);
        userProjectRepository.save(userProject);

        return created;
    }

    public Project update(Integer id, Project upProject) {
        return projectRepository.findById(id)
                .map(existing -> {
                    if (!existing.getName().equalsIgnoreCase(upProject.getName())) {
                        List<UserProject> members = userProjectRepository.findByProyectoId(id);
                        for (UserProject up : members) {
                            Optional<Project> duplicado = projectRepository.findByUsuarioIdAndNombre(
                                up.getUsuario().getId(), 
                                upProject.getName()
                            );
                            if (duplicado.isPresent() && !duplicado.get().getId().equals(id)) {
                                throw new RuntimeException("Ya existe un proyecto con este nombre para uno de los miembros");
                            }
                        }
                    }
                    
                    existing.setName(upProject.getName());
                    existing.setDescription(upProject.getDescription());
                    return projectRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
    }

    @Transactional
    public void delete(Integer id) {
        Project project = projectRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        userProjectRepository.deleteAll(userProjectRepository.findByProyectoId(id));

        projectRepository.delete(project);
    }
}