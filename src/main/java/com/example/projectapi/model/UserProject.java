package com.example.projectapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario_proyecto")
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

    public UserProject() {
    }

    public UserProject(User usuario, Project proyecto, Rol rol) {
        this.usuario = usuario;
        this.proyecto = proyecto;
        this.rol = rol;
        this.id = new UserProjectId(usuario.getId(), proyecto.getId());
    }

    public UserProjectId getId() { return id; }
    public void setId(UserProjectId id) { this.id = id; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public Project getProyecto() { return proyecto; }
    public void setProyecto(Project proyecto) { this.proyecto = proyecto; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
