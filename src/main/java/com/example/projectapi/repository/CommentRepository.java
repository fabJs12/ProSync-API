package com.example.projectapi.repository;

import com.example.projectapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTaskId(Integer taskId);
    List<Comment> findByUserId(Integer userId);
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Integer taskId);
}