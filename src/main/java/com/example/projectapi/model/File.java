package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "archivos", schema = "public")
@Getter @Setter
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "id_tarea",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_archivos_tarea")
    )
    private Task task;

    @Column(name = "archivo_url", nullable = false, columnDefinition = "TEXT")
    private String archivoUrl;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // Constructor con parámetros
    public File(Task task, String archivoUrl) {
        this.task = task;
        this.archivoUrl = archivoUrl;
    }
}