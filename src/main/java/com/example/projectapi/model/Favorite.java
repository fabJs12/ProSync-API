package com.example.projectapi.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "favoritos", schema = "public")
@IdClass(Favorite.FavoriteId.class)
public class Favorite {

    @Id
    @ManyToOne
    @JoinColumn(
            name = "id_usuario",
            foreignKey = @ForeignKey(name = "fk_favoritos_usuario")
    )
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(
            name = "id_tarea",
            foreignKey = @ForeignKey(name = "fk_favoritos_tarea")
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
        private Integer user;
        private Integer task;

        public FavoriteId() {}

        public FavoriteId(Integer user, Integer task) {
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
