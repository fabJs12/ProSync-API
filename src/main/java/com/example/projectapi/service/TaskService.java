package com.example.projectapi.service;

import com.example.projectapi.dto.TaskDTO;
import com.example.projectapi.model.Board;
import com.example.projectapi.model.Estado;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
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

    public TaskService(TaskRepository taskRepository, BoardRepository boardRepository, EstadoRepository estadoRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.boardRepository = boardRepository;
        this.estadoRepository = estadoRepository;
        this.userRepository = userRepository;
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
        return convertToDTO(created);
    }

    public Task update(Integer id, Task tarea) {
        return taskRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(tarea.getTitle());
                    existing.setDescription(tarea.getDescription());
                    existing.setDueDate(tarea.getDueDate());

                    if(tarea.getEstado() != null) {
                        Estado estado = estadoRepository.findById(tarea.getEstado().getId())
                                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
                        existing.setEstado(estado);
                    }

                    if(tarea.getResponsable() != null) {
                        User responsable = userRepository.findById(tarea.getResponsable().getId())
                                .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));
                        existing.setResponsable(responsable);
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