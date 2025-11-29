package com.example.projectapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tareas", schema = "public")
@Getter @Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String title;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_board", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private User responsable;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT now()")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // Relación con notificaciones - se borran en cascada cuando se borra la tarea
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    public Task(String title, String description, Estado estado, OffsetDateTime dueDate, Board board, User responsable) {
        this.title = title;
        this.description = description;
        this.estado = estado;
        this.dueDate = dueDate;
        this.board = board;
        this.responsable = responsable;
    }
}