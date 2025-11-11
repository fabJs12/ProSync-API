package com.example.projectapi.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.projectapi.util.JsonNodeConverter;

@Entity
@Table(name = "integraciones",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "integraciones_id_usuario_nombre_servicio_key",
                        columnNames = {"id_usuario", "nombre_servicio"}
                )
        })
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_integraciones_usuario")
    )
    private User user;

    @Column(name = "nombre_servicio", nullable = false, length = 100)
    private String nombreServicio;

    @Convert(converter = JsonNodeConverter.class)
    @Column(name = "detalles", columnDefinition = "jsonb")
    private JsonNode detalles;

    // Constructor vacío
    public Integration() {}

    // Constructor con parámetros
    public Integration(User user, String nombreServicio, JsonNode detalles) {
        this.user = user;
        this.nombreServicio = nombreServicio;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public JsonNode getDetalles() {
        return detalles;
    }

    public void setDetalles(JsonNode detalles) {
        this.detalles = detalles;
    }
}