package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "boards",
        schema = "public",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_proyecto", "nombre"})
        }
)
@Getter @Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    // Relación con la tabla "projects"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", foreignKey = @ForeignKey(name = "fk_boards_proyecto"))
    private Project project;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT now()")
    private OffsetDateTime createdAt;

    public Board(String nombre, Project project) {
        this.nombre = nombre;
        this.project = project;
    }
}