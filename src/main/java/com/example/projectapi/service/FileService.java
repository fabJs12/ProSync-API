package com.example.projectapi.service;

import com.example.projectapi.model.TaskFile;
import com.example.projectapi.model.Task;
import com.example.projectapi.repository.FileRepository;
import com.example.projectapi.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final TaskRepository taskRepository;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository, TaskRepository taskRepository, StorageService storageService) {
        this.fileRepository = fileRepository;
        this.taskRepository = taskRepository;
        this.storageService = storageService;
    }

    public List<TaskFile> findAll() {
        return fileRepository.findAll();
    }

    public Optional<TaskFile> findById(Integer id) {
        return fileRepository.findById(id);
    }

    public List<TaskFile> findByTaskId(Integer taskId) {
        return fileRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
    }

    public TaskFile uploadFile(Integer taskId, MultipartFile multipartFile) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        String urlGenerada = storageService.uploadFile(multipartFile);

        TaskFile file = new TaskFile();
        file.setTask(task);
        file.setArchivoUrl(urlGenerada);

        return fileRepository.save(file);
    }

    public void delete(Integer id) {
        if (!fileRepository.existsById(id)) {
            throw new RuntimeException("Archivo no encontrado");
        }
        fileRepository.deleteById(id);
    }
}