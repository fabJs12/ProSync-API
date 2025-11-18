package com.example.projectapi.service;

import com.example.projectapi.model.UserProject;
import com.example.projectapi.model.UserProjectId;
import com.example.projectapi.model.User;
import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import com.example.projectapi.repository.UserProjectRepository;
import com.example.projectapi.repository.UserRepository;
import com.example.projectapi.repository.ProjectRepository;
import com.example.projectapi.repository.RolRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserProjectService {
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RolRepository rolRepository;

    public UserProjectService(UserProjectRepository userProjectRepository,
                              UserRepository userRepository,
                              ProjectRepository projectRepository,
                              RolRepository rolRepository) {
        this.userProjectRepository = userProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.rolRepository = rolRepository;
    }

    public List<UserProject> findAll() {
        return userProjectRepository.findAll();
    }

    public Optional<UserProject> findById(UserProjectId id) {
        return userProjectRepository.findById(id);
    }

    public List<UserProject> findByUserId(Integer userId) {
        return userProjectRepository.findByUsuarioId(userId);
    }

    public List<UserProject> findByProjectId(Integer projectId) {
        return userProjectRepository.findByProyectoId(projectId);
    }

    public boolean usuarioTieneRelacion(Integer userId, Integer projectId) {
        return userProjectRepository.existsById(new UserProjectId(userId, projectId));
    }

    public List<UserProject> findByRolId(Integer rolId) {
        return userProjectRepository.findByRolId(rolId);
    }

    public UserProject create(Integer userId, Integer projectId, Integer rolId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UserProjectId id = new UserProjectId(userId, projectId);
        if (userProjectRepository.existsById(id)) {
            throw new RuntimeException("El usuario ya está asignado a este proyecto");
        }

        UserProject userProject = new UserProject(user, project, rol);
        return userProjectRepository.save(userProject);
    }

    public UserProject update(Integer userId, Integer projectId, Integer newRolId) {
        UserProjectId id = new UserProjectId(userId, projectId);

        return userProjectRepository.findById(id)
                .map(existing -> {
                    Rol newRol = rolRepository.findById(newRolId)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    existing.setRol(newRol);
                    return userProjectRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
    }

    public void delete(Integer userId, Integer projectId) {
        UserProjectId id = new UserProjectId(userId, projectId);
        if (!userProjectRepository.existsById(id)) {
            throw new RuntimeException("Asignación no encontrada");
        }
        userProjectRepository.deleteById(id);
    }
}