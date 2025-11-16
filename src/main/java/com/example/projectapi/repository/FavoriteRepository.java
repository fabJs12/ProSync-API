package com.example.projectapi.repository;

import com.example.projectapi.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Favorite.FavoriteId> {
    List<Favorite> findByUserId(Integer userId);
    List<Favorite> findByTaskId(Integer taskId);
    boolean existsByUserIdAndTaskId(Integer userId, Integer taskId);
}