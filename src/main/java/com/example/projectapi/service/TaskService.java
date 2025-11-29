package com.example.projectapi.service;

import com.example.projectapi.dto.TaskDTO;
import com.example.projectapi.model.*;
import com.example.projectapi.repository.BoardRepository;
import com.example.projectapi.repository.EstadoRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;
    private final EstadoRepository estadoRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TaskService(TaskRepository taskRepository, BoardRepository boardRepository, EstadoRepository estadoRepository, UserRepository userRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
        this.estadoRepository = estadoRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<Task> findByBoard(Integer boardId) {
        return taskRepository.findByBoardId(boardId);
    }

    public List<TaskDTO> findByBoardDTO(Integer boardId) {
        return taskRepository.findByBoardId(boardId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
    }

    public Optional<TaskDTO> findByIdDTO(Integer id) {
        return taskRepository.findById(id).map(this::convertToDTO);
    }

    public List<Task> findByEstado(Integer estadoId) {
        return taskRepository.findByEstadoId(estadoId);
    }

    public List<TaskDTO> findByEstadoDTO(Integer estadoId) {
        return taskRepository.findByEstadoId(estadoId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Task> findByResponsable(Integer responsableId) {
        return taskRepository.findByResponsableId(responsableId);
    }

    public List<TaskDTO> findByResponsableDTO(Integer responsableId) {
        return taskRepository.findByResponsableId(responsableId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> findByUsuarioDTO(Integer userId) {
        return taskRepository.findByResponsableId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Task create(Task task, Integer boardId, Integer estadoId, Integer responsableId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board no encontrado"));

        Estado estado = estadoRepository.findById(estadoId)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        task.setBoard(board);
        task.setEstado(estado);

        if(responsableId != null) {
            User responsable = userRepository.findById(responsableId)
                    .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

            task.setResponsable(responsable);
        }

        return taskRepository.save(task);
    }

    public TaskDTO createDTO(Task task, Integer boardId, Integer estadoId, Integer responsableId) {
        Task created = create(task, boardId, estadoId, responsableId);

        if (responsableId != null) {
            User responsable = userRepository.findById(responsableId)
                    .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

            String mensaje = "Te han asignado la tarea: " + created.getTitle();

            notificationService.create(
                    responsable,
                    created,
                    mensaje,
                    Notification.NotificationType.TASK_ASSIGNED
            );
        }
        return convertToDTO(created);
    }

    public Task update(Integer id, Task tareaConDatosNuevos) {
        return taskRepository.findById(id)
                .map(existing -> {

                    boolean cambioContenido = tareaConDatosNuevos.getTitle() != null ||
                            tareaConDatosNuevos.getDescription() != null ||
                            tareaConDatosNuevos.getDueDate() != null;

                    if (tareaConDatosNuevos.getTitle() != null) {
                        existing.setTitle(tareaConDatosNuevos.getTitle());
                    }

                    if (tareaConDatosNuevos.getDescription() != null) {
                        existing.setDescription(tareaConDatosNuevos.getDescription());
                    }

                    if (tareaConDatosNuevos.getDueDate() != null) {
                        existing.setDueDate(tareaConDatosNuevos.getDueDate());
                    }

                    if(tareaConDatosNuevos.getEstado() != null && tareaConDatosNuevos.getEstado().getId() != null) {
                        Estado estado = estadoRepository.findById(tareaConDatosNuevos.getEstado().getId())
                                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
                        existing.setEstado(estado);
                    }

                    boolean cambioResponsable = false;

                    if(tareaConDatosNuevos.getResponsable() != null) {
                        if (tareaConDatosNuevos.getResponsable().getId() != null) {
                            User responsable = userRepository.findById(tareaConDatosNuevos.getResponsable().getId())
                                    .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

                            User anteriorResponsable = existing.getResponsable();

                            if (anteriorResponsable == null || !anteriorResponsable.getId().equals(responsable.getId())) {
                                existing.setResponsable(responsable);
                                cambioResponsable = true;

                                notificationService.createSafely(
                                        responsable,
                                        existing,
                                        "La tarea ha sido actualizada: " + existing.getTitle(),
                                        Notification.NotificationType.TASK_ASSIGNED
                                );
                            }
                        } else {
                            existing.setResponsable(null);
                            cambioResponsable = true;
                        }
                    }

                    if (cambioContenido && existing.getResponsable() != null && !cambioResponsable) {
                        notificationService.createSafely(
                                existing.getResponsable(),
                                existing,
                                "La tarea ha sido actualizada: " + existing.getTitle(),
                                Notification.NotificationType.TASK_UPDATED
                        );
                    }

                    return taskRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));
    }

    public TaskDTO updateDTO(Integer id, Task tarea) {
        Task updated = update(id, tarea);
        return convertToDTO(updated);
    }

    public void delete(Integer id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());

        if (task.getBoard() != null) {
            dto.setBoardId(task.getBoard().getId());
            dto.setBoardName(task.getBoard().getName());
            if (task.getBoard().getProject() != null) {
                dto.setProjectId(task.getBoard().getProject().getId());
                dto.setProjectName(task.getBoard().getProject().getName());
            }
        }

        if (task.getEstado() != null) {
            dto.setEstadoId(task.getEstado().getId());
            dto.setEstadoNombre(task.getEstado().getEstado());
        }

        if (task.getResponsable() != null) {
            dto.setResponsableId(task.getResponsable().getId());
            dto.setResponsableUsername(task.getResponsable().getUsername());
        }

        return dto;
    }
}