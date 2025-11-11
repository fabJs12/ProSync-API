package com.example.projectapi.service;

import com.example.projectapi.model.File;
import com.example.projectapi.model.Task;
import com.example.projectapi.repository.FileRepository;
import com.example.projectapi.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;

    public FileService(FileRepository fileRepository, TaskRepository taskRepository) {
        this.fileRepository = fileRepository;
        this.taskRepository = taskRepository;
    }

    public List<File> findAll() {
        return fileRepository.findAll();
    }

    public Optional<File> findById(Integer id) {
        return fileRepository.findById(id);
    }

    public List<File> findByTaskId(Integer taskId) {
        return fileRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    public File create(Integer taskId, String archivoUrl) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        File file = new File();
        file.setTask(task);
        file.setArchivoUrl(archivoUrl);

        return fileRepository.save(file);
    }

    public File update(Integer id, String archivoUrl) {
        return fileRepository.findById(id)
                .map(existing -> {
                    existing.setArchivoUrl(archivoUrl);
                    return fileRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado"));
    }

    public void delete(Integer id) {
        if (!fileRepository.existsById(id)) {
            throw new RuntimeException("Archivo no encontrado");
        }
        fileRepository.deleteById(id);
    }
}