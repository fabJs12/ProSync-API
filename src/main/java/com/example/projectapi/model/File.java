package com.example.projectapi.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "archivos", schema = "public")
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
    private OffsetDateTime createdAt;

    // Constructor vacío (obligatorio por JPA)
    public File() {}

    // Constructor con parámetros
    public File(Task task, String archivoUrl) {
        this.task = task;
        this.archivoUrl = archivoUrl;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setArchivoUrl(String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}