package com.example.projectapi.service;

import com.example.projectapi.model.Notification;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.NotificationRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               TaskRepository taskRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> findById(Integer id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> findByUserId(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> findByTaskId(Integer taskId) {
        return notificationRepository.findByTaskId(taskId);
    }

    public Notification create(Integer userId, Integer taskId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTask(task);
        notification.setMessage(message);

        return notificationRepository.save(notification);
    }

    public Notification update(Integer id, String message) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setMessage(message);
                    return notificationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    public void delete(Integer id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada");
        }
        notificationRepository.deleteById(id);
    }

    public void deleteByUserId(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);
    }
}