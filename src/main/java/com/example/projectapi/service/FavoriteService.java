package com.example.projectapi.service;

import com.example.projectapi.model.Favorite;
import com.example.projectapi.model.Task;
import com.example.projectapi.model.User;
import com.example.projectapi.repository.FavoriteRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public Favorite addFavorite(Integer userId, Integer taskId) {
        if(favoriteRepository.existsByUserIdAndTaskId(userId,taskId)) {
            throw new RuntimeException("Esta tarea ya está en favoritos");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrado"));

        Favorite favorite = new Favorite(user,task);

        return favoriteRepository.save(favorite);
    }

    public void removeFavorite(Integer userId, Integer taskId) {
        Favorite.FavoriteId id = new Favorite.FavoriteId(userId,taskId);

        favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));

        favoriteRepository.deleteById(id);
    }

    public List<Task> listFavoritesByUser(Integer userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getTask)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Integer userId, Integer taskId) {
        return favoriteRepository.existsByUserIdAndTaskId(userId, taskId);
    }
}
