package com.example.projectapi.repository;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(Integer userId, Rol rol);
    List<Project> findByUsuariosAsociadosUsuarioId(Integer userId);
}
