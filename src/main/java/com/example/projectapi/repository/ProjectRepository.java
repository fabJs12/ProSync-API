package com.example.projectapi.repository;

import com.example.projectapi.model.Project;
import com.example.projectapi.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findByUsuariosAsociadosUsuarioIdAndUsuariosAsociadosRol(Integer userId, Rol rol);
    List<Project> findByUsuariosAsociadosUsuarioId(Integer userId);
    
    @Query("SELECT p FROM Project p JOIN p.usuariosAsociados up WHERE up.usuario.id = :userId AND LOWER(p.name) = LOWER(:nombre)")
    Optional<Project> findByUsuarioIdAndNombre(@Param("userId") Integer userId, @Param("nombre") String nombre);
}