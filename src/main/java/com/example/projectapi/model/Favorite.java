package com.example.projectapi.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "favorites", schema = "public")
@IdClass(Favorite.FavoriteId.class)
public class Favorite {

    @Id
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "favorites_user_id_fkey")
    )
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "task_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "favorites_task_id_fkey")
    )
    private Task task;

    // Constructor vacío obligatorio
    public Favorite() {}

    // Constructor con parámetros
    public Favorite(User user, Task task) {
        this.user = user;
        this.task = task;
    }

    // Getters y setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    // Clase interna para la clave compuesta
    public static class FavoriteId implements Serializable {
        private Long user;
        private Long task;

        public FavoriteId() {}

        public FavoriteId(Long user, Long task) {
            this.user = user;
            this.task = task;
        }

        // equals() y hashCode() son obligatorios para IdClass
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FavoriteId)) return false;
            FavoriteId that = (FavoriteId) o;
            return user.equals(that.user) && task.equals(that.task);
        }

        @Override
        public int hashCode() {
            return user.hashCode() + task.hashCode();
        }
    }
}
