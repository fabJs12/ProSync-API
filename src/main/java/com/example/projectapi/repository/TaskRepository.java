package com.example.projectapi.repository;

import com.example.projectapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByBoardId(Integer boardId);
    List<Task> findByResponsableId(Integer responsibleId);
    List<Task> findByEstadoId(Integer estadoId);
}