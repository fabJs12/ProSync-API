package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "comentarios")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con la tarea
    @ManyToOne
    @JoinColumn(name = "id_tarea", foreignKey = @ForeignKey(name = "fk_comentarios_tarea"))
    private Task task;

    // Relación con el usuario que hizo el comentario
    @ManyToOne
    @JoinColumn(name = "id_usuario", foreignKey = @ForeignKey(name = "fk_comentarios_usuario"))
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT now()")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // Constructor con parámetros
    public Comment(Task task, User user, String contenido, OffsetDateTime createdAt) {
        this.task = task;
        this.user = user;
        this.contenido = contenido;
        this.createdAt = createdAt;
    }
}