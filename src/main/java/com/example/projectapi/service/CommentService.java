package com.example.projectapi.service;

import com.example.projectapi.model.Comment;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.CommentRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    public Optional<Comment> findById(Integer id) {
        return commentRepository.findById(id);
    }

    public List<Comment> findByTaskId(Integer taskId) {
        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    public List<Comment> findByUserId(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    public Comment create(Integer taskId, Integer userId, String contenido) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setContenido(contenido);

        return commentRepository.save(comment);
    }

    public Comment update(Integer id, String contenido) {
        return commentRepository.findById(id)
                .map(existing -> {
                    existing.setContenido(contenido);
                    return commentRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
    }

    public void delete(Integer id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comentario no encontrado");
        }
        commentRepository.deleteById(id);
    }
}