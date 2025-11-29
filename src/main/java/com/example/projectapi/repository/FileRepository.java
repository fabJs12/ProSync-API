package com.example.projectapi.repository;

import com.example.projectapi.model.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<TaskFile, Integer> {
    List<TaskFile> findByTaskIdOrderByCreatedAtDesc(Integer taskId);
}