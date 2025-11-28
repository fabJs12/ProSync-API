package com.example.projectapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario_proyecto")
@Getter @Setter
@NoArgsConstructor
public class UserProject{
    @EmbeddedId
    private UserProjectId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProyecto")
    @JoinColumn(name = "id_proyecto")
    private Project proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    public UserProject(User usuario, Project proyecto, Rol rol) {
        this.usuario = usuario;
        this.proyecto = proyecto;
        this.rol = rol;
        this.id = new UserProjectId(usuario.getId(), proyecto.getId());
    }
}
