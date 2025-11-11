package com.example.projectapi.repository;

import com.example.projectapi.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByTaskId(Integer taskId);
    List<File> findByTaskIdOrderByCreatedAtDesc(Integer taskId);
}