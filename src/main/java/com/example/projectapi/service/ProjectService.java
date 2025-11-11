package com.example.projectapi.service;

import com.example.projectapi.model.Project;
import com.example.projectapi.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

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
