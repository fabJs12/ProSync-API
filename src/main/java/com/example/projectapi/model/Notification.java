package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notificaciones", schema = "public")
@Getter @Setter
@NoArgsConstructor
public class Notification {

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
    private Task task;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "leida", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean leida = false;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    // Constructor con parámetros
    public Notification(User user, Task task, String mensaje) {
        this.user = user;
        this.task = task;
        this.mensaje = mensaje;
        this.leida = false;
    }
}