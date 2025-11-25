package com.example.projectapi.dto;

public class DashboardStatsDTO {
    private Integer proyectosActivos;
    private Integer cambioProyectos;
    private Integer tareasCompletadas;
    private Integer cambioTareas;
    private Integer miembrosEquipo;
    private Integer cambioMiembros;
    private Double tiempoPromedio;
    private Double cambioTiempo;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(Integer proyectosActivos, Integer cambioProyectos, 
                            Integer tareasCompletadas, Integer cambioTareas,
                            Integer miembrosEquipo, Integer cambioMiembros,
                            Double tiempoPromedio, Double cambioTiempo) {
        this.proyectosActivos = proyectosActivos;
        this.cambioProyectos = cambioProyectos;
        this.tareasCompletadas = tareasCompletadas;
        this.cambioTareas = cambioTareas;
        this.miembrosEquipo = miembrosEquipo;
        this.cambioMiembros = cambioMiembros;
        this.tiempoPromedio = tiempoPromedio;
        this.cambioTiempo = cambioTiempo;
    }

    public Integer getProyectosActivos() {
        return proyectosActivos;
    }

    public void setProyectosActivos(Integer proyectosActivos) {
        this.proyectosActivos = proyectosActivos;
    }

    public Integer getCambioProyectos() {
        return cambioProyectos;
    }

    public void setCambioProyectos(Integer cambioProyectos) {
        this.cambioProyectos = cambioProyectos;
    }

    public Integer getTareasCompletadas() {
        return tareasCompletadas;
    }

    public void setTareasCompletadas(Integer tareasCompletadas) {
        this.tareasCompletadas = tareasCompletadas;
    }

    public Integer getCambioTareas() {
        return cambioTareas;
    }

    public void setCambioTareas(Integer cambioTareas) {
        this.cambioTareas = cambioTareas;
    }

    public Integer getMiembrosEquipo() {
        return miembrosEquipo;
    }

    public void setMiembrosEquipo(Integer miembrosEquipo) {
        this.miembrosEquipo = miembrosEquipo;
    }

    public Integer getCambioMiembros() {
        return cambioMiembros;
    }

    public void setCambioMiembros(Integer cambioMiembros) {
        this.cambioMiembros = cambioMiembros;
    }

    public Double getTiempoPromedio() {
        return tiempoPromedio;
    }

    public void setTiempoPromedio(Double tiempoPromedio) {
        this.tiempoPromedio = tiempoPromedio;
    }

    public Double getCambioTiempo() {
        return cambioTiempo;
    }

    public void setCambioTiempo(Double cambioTiempo) {
        this.cambioTiempo = cambioTiempo;
    }
}