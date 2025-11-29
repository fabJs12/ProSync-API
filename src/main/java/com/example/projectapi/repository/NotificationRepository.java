package com.example.projectapi.repository;

import com.example.projectapi.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    Page<Notification> findByUserIdAndLeidaOrderByCreatedAtDesc(Integer userId, Boolean leida, Pageable pageable);
    
    List<Notification> findByTaskId(Integer taskId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.leida = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.leida = true, n.fechaLectura = CURRENT_TIMESTAMP WHERE n.user.id = :userId AND n.leida = false")
    int markAllAsReadByUserId(@Param("userId") Integer userId);
}