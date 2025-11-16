package com.example.projectapi.service;

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

    public Optional<Task> findById(Integer id) {
        return taskRepository.findById(id);
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

    public void delete(Integer id) {
        taskRepository.deleteById(id);
    }
}
