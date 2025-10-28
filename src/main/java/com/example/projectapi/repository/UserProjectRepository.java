package com.example.projectapi.repository;

import com.example.projectapi.model.UserProject;
import com.example.projectapi.model.UserProjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    List<UserProject> findByUsuarioId(Integer userId);
    List<UserProject> findByProyectoId(Integer projectId);
    List<UserProject> findByRolId(Integer rolId);
}