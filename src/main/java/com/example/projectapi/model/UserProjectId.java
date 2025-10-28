package com.example.projectapi.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserProjectId implements Serializable{
    private Integer idUsuario; // Mapea a id_usuario
    private Integer idProyecto; // Mapea a id_proyecto

    public UserProjectId() {}

    public UserProjectId(Integer idUsuario, Integer idProyecto) {
        this.idUsuario = idUsuario;
        this.idProyecto = idProyecto;
    }

    public Integer getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdProyecto() { return idProyecto; }

    public void setIdProyecto(Integer idProyecto) { this.idProyecto = idProyecto; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProjectId that = (UserProjectId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
                Objects.equals(idProyecto, that.idProyecto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idProyecto);
    }
}
