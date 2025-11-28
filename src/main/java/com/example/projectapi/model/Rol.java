package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "rol", nullable = false, unique = true)
    private String rol;

    public Rol(String rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol1 = (Rol) o;
        return Objects.equals(id, rol1.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}