package com.example.projectapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "archivos", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class TaskFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tarea", nullable = false, foreignKey = @ForeignKey(name = "fk_archivos_tarea"))
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "board", "estado", "responsable", "files" })
    private Task task;

    @Column(name = "archivo_url", nullable = false, columnDefinition = "TEXT")
    private String archivoUrl;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id") // Nombre de la columna en BD
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "roles", "projects" }) // Evita bucles y
                                                                                                      // datos sensibles
    private User autor;

    // Constructor con parámetros
    public TaskFile(Task task, String archivoUrl) {
        this.task = task;
        this.archivoUrl = archivoUrl;
    }
}