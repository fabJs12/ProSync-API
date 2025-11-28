package com.example.projectapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Integer proyectosActivos;
    private Integer cambioProyectos;
    private Integer tareasCompletadas;
    private Integer cambioTareas;
    private Integer miembrosEquipo;
    private Integer cambioMiembros;
    private Double tiempoPromedio;
    private Double cambioTiempo;
}