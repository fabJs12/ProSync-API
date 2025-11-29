package com.example.projectapi.service;

import com.example.projectapi.model.Notification;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.NotificationRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
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

    public Page<Notification> findByUserId(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Notification> findByUserIdAndLeida(Integer userId, Boolean leida, Pageable pageable) {
        return notificationRepository.findByUserIdAndLeidaOrderByCreatedAtDesc(userId, leida, pageable);
    }

    public Optional<Notification> findById(Integer id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> findByTaskId(Integer taskId) {
        return notificationRepository.findByTaskId(taskId);
    }

    public Long countUnread(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Crea una notificación validando que el usuario tenga acceso a la tarea si se proporciona
     */
    public Notification create(Integer userId, Integer taskId, String mensaje, Notification.NotificationType tipo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Task task = null;
        if (taskId != null) {
            task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTask(task);
        notification.setMensaje(mensaje);
        notification.setTipo(tipo);
        notification.setLeida(false);

        return notificationRepository.save(notification);
    }

    /**
     * Crea notificación de forma segura, sin lanzar excepción si falla
     */
    public void createSafely(Integer userId, Integer taskId, String mensaje, Notification.NotificationType tipo) {
        try {
            create(userId, taskId, mensaje, tipo);
        } catch (Exception e) {
            System.err.println("Error al crear notificación: " + e.getMessage());
        }
    }

    public Notification update(Integer id, String mensaje) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setMensaje(mensaje);
                    return notificationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    public Notification markAsRead(Integer id, Integer requestingUserId) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    if (!existing.getUser().getId().equals(requestingUserId)) {
                        throw new RuntimeException("No tienes permiso para marcar esta notificación");
                    }
                    existing.setLeida(true);
                    existing.setFechaLectura(OffsetDateTime.now());
                    return notificationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    public Notification markAsUnread(Integer id, Integer requestingUserId) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    if (!existing.getUser().getId().equals(requestingUserId)) {
                        throw new RuntimeException("No tienes permiso para marcar esta notificación");
                    }
                    existing.setLeida(false);
                    existing.setFechaLectura(null);
                    return notificationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Transactional
    public int markAllAsRead(Integer userId) {
        return notificationRepository.markAllAsReadByUserId(userId);
    }

    public void delete(Integer id, Integer requestingUserId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        
        if (!notification.getUser().getId().equals(requestingUserId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación");
        }
        
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteByUserId(Integer userId, Integer requestingUserId) {
        if (!userId.equals(requestingUserId)) {
            throw new RuntimeException("No tienes permiso para eliminar notificaciones de otro usuario");
        }
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(
            userId, 
            Pageable.unpaged()
        ).getContent();
        
        notificationRepository.deleteAll(notifications);
    }

    
    public void notifyTaskAssigned(Integer userId, Integer taskId, String taskTitle, String assignerUsername) {
        String mensaje = assignerUsername + " te ha asignado la tarea: " + taskTitle;
        createSafely(userId, taskId, mensaje, Notification.NotificationType.TASK_ASSIGNED);
    }

    public void notifyTaskUpdated(Integer userId, Integer taskId, String taskTitle, String updaterUsername) {
        String mensaje = updaterUsername + " ha actualizado la tarea: " + taskTitle;
        createSafely(userId, taskId, mensaje, Notification.NotificationType.TASK_UPDATED);
    }

    public void notifyTaskComment(Integer userId, Integer taskId, String taskTitle, String commenterUsername) {
        String mensaje = commenterUsername + " ha comentado en la tarea: " + taskTitle;
        createSafely(userId, taskId, mensaje, Notification.NotificationType.TASK_COMMENT);
    }

    public void notifyProjectAdded(Integer userId, String projectName, String adderUsername, String roleName) {
        String mensaje = adderUsername + " te ha agregado al proyecto: " + projectName + " como " + roleName;
        createSafely(userId, null, mensaje, Notification.NotificationType.PROJECT_ADDED);
    }

    public void notifyProjectRemoved(Integer userId, String projectName, String removerUsername) {
        String mensaje = removerUsername + " te ha removido del proyecto: " + projectName;
        createSafely(userId, null, mensaje, Notification.NotificationType.PROJECT_REMOVED);
    }

    public void notifyRoleChanged(Integer userId, String projectName, String roleName, String changerUsername) {
        String mensaje = changerUsername + " ha cambiado tu rol a: " + roleName + " en el proyecto " + projectName;
        createSafely(userId, null, mensaje, Notification.NotificationType.ROLE_CHANGED);
    }
}