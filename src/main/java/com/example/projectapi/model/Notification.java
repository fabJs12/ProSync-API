package com.example.projectapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notificaciones", schema = "public", indexes = {
    @Index(name = "idx_notificaciones_usuario", columnList = "id_usuario"),
    @Index(name = "idx_notificaciones_leida", columnList = "leida"),
    @Index(name = "idx_notificaciones_usuario_leida", columnList = "id_usuario,leida")
})
@Getter @Setter
@NoArgsConstructor
public class Notification {

    public enum NotificationType {
        TASK_ASSIGNED,
        TASK_UPDATED,
        TASK_COMMENT,
        PROJECT_ADDED,
        PROJECT_REMOVED,
        ROLE_CHANGED,
        DEADLINE_NEAR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_notificaciones_usuario")
    )
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "id_tarea",
            foreignKey = @ForeignKey(name = "fk_notificaciones_tarea")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "notifications", "board"})
    private Task task;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 50)
    private NotificationType tipo;

    @Column(name = "leida", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean leida = false;

    @Column(name = "fecha_lectura", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime fechaLectura;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // Constructor con parámetros
    public Notification(User user, Task task, String mensaje, NotificationType tipo) {
        this.user = user;
        this.task = task;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.leida = false;
    }
}