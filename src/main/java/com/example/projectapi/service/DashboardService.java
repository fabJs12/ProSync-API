package com.example.projectapi.service;

import com.example.projectapi.dto.DashboardStatsDTO;
import com.example.projectapi.model.Estado;
import com.example.projectapi.model.Project;
import com.example.projectapi.model.Task;
import com.example.projectapi.repository.EstadoRepository;
import com.example.projectapi.repository.ProjectRepository;
import com.example.projectapi.repository.TaskRepository;
import com.example.projectapi.repository.UserProjectRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DashboardService {
    
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserProjectRepository userProjectRepository;
    private final EstadoRepository estadoRepository;

    public DashboardService(ProjectRepository projectRepository, 
                           TaskRepository taskRepository,
                           UserProjectRepository userProjectRepository,
                           EstadoRepository estadoRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userProjectRepository = userProjectRepository;
        this.estadoRepository = estadoRepository;
    }

    public DashboardStatsDTO getStats(Integer userId) {
        OffsetDateTime ahora = OffsetDateTime.now();
        OffsetDateTime inicioMesActual = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);

        Integer proyectosActivos = countProyectosActivos(userId);
        Integer proyectosActivosMesAnterior = countProyectosActivosMesAnterior(userId, inicioMesActual);
        Integer cambioProyectos = proyectosActivos - proyectosActivosMesAnterior;

        Estado estadoCompletado = estadoRepository.findById(3).orElse(null);
        Integer tareasCompletadas = 0;
        Integer tareasCompletadasMesAnterior = 0;
        
        if (estadoCompletado != null) {
            tareasCompletadas = countTareasCompletadas(userId, estadoCompletado.getId());
            tareasCompletadasMesAnterior = countTareasCompletadasMesAnterior(userId, estadoCompletado.getId(), inicioMesActual);
        }
        
        Integer cambioTareas = tareasCompletadas - tareasCompletadasMesAnterior;

        Integer miembrosEquipo = countMiembrosEquipo(userId);
        Integer miembrosMesAnterior = countMiembrosMesAnterior(userId, inicioMesActual);
        Integer cambioMiembros = miembrosEquipo - miembrosMesAnterior;

        Double tiempoPromedio = calculateTiempoPromedio(userId, estadoCompletado != null ? estadoCompletado.getId() : null);
        Double tiempoPromedioMesAnterior = calculateTiempoPromedioMesAnterior(userId, estadoCompletado != null ? estadoCompletado.getId() : null, inicioMesActual);
        Double cambioTiempo = tiempoPromedio - tiempoPromedioMesAnterior;

        return new DashboardStatsDTO(
            proyectosActivos,
            cambioProyectos,
            tareasCompletadas,
            cambioTareas,
            miembrosEquipo,
            cambioMiembros,
            tiempoPromedio,
            cambioTiempo
        );
    }

    private Integer countProyectosActivos(Integer userId) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        return proyectos.size();
    }

    private Integer countProyectosActivosMesAnterior(Integer userId, OffsetDateTime inicioMesActual) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        return (int) proyectos.stream()
            .filter(p -> p.getCreatedAt().isBefore(inicioMesActual))
            .count();
    }

    private Integer countTareasCompletadas(Integer userId, Integer estadoCompletadoId) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        return proyectos.stream()
            .flatMap(p -> taskRepository.findByBoardId(p.getId()).stream())
            .filter(t -> t.getEstado() != null && t.getEstado().getId().equals(estadoCompletadoId))
            .mapToInt(t -> 1)
            .sum();
    }

    private Integer countTareasCompletadasMesAnterior(Integer userId, Integer estadoCompletadoId, OffsetDateTime inicioMesActual) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        return proyectos.stream()
            .flatMap(p -> taskRepository.findByBoardId(p.getId()).stream())
            .filter(t -> t.getEstado() != null && 
                        t.getEstado().getId().equals(estadoCompletadoId) &&
                        t.getCreatedAt().isBefore(inicioMesActual))
            .mapToInt(t -> 1)
            .sum();
    }

    private Integer countMiembrosEquipo(Integer userId) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        return proyectos.stream()
            .flatMap(p -> userProjectRepository.findByProyectoId(p.getId()).stream())
            .map(up -> up.getUsuario().getId())
            .distinct()
            .mapToInt(id -> 1)
            .sum();
    }

    private Integer countMiembrosMesAnterior(Integer userId, OffsetDateTime inicioMesActual) {
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        List<Project> proyectosMesAnterior = proyectos.stream()
            .filter(p -> p.getCreatedAt().isBefore(inicioMesActual))
            .toList();
        
        return proyectosMesAnterior.stream()
            .flatMap(p -> userProjectRepository.findByProyectoId(p.getId()).stream())
            .map(up -> up.getUsuario().getId())
            .distinct()
            .mapToInt(id -> 1)
            .sum();
    }

    private Double calculateTiempoPromedio(Integer userId, Integer estadoCompletadoId) {
        if (estadoCompletadoId == null) return 0.0;
        
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        List<Task> tareasCompletadas = proyectos.stream()
            .flatMap(p -> taskRepository.findByBoardId(p.getId()).stream())
            .filter(t -> t.getEstado() != null && t.getEstado().getId().equals(estadoCompletadoId))
            .filter(t -> t.getCreatedAt() != null && t.getDueDate() != null)
            .toList();

        if (tareasCompletadas.isEmpty()) return 0.0;

        double sumaDias = tareasCompletadas.stream()
            .mapToDouble(t -> {
                Duration duration = Duration.between(t.getCreatedAt(), t.getDueDate());
                return duration.toDays();
            })
            .sum();

        return Math.round((sumaDias / tareasCompletadas.size()) * 10.0) / 10.0;
    }

    private Double calculateTiempoPromedioMesAnterior(Integer userId, Integer estadoCompletadoId, OffsetDateTime inicioMesActual) {
        if (estadoCompletadoId == null) return 0.0;
        
        List<Project> proyectos = projectRepository.findByUsuariosAsociadosUsuarioId(userId);
        
        List<Task> tareasCompletadas = proyectos.stream()
            .flatMap(p -> taskRepository.findByBoardId(p.getId()).stream())
            .filter(t -> t.getEstado() != null && 
                        t.getEstado().getId().equals(estadoCompletadoId) &&
                        t.getCreatedAt().isBefore(inicioMesActual))
            .filter(t -> t.getCreatedAt() != null && t.getDueDate() != null)
            .toList();

        if (tareasCompletadas.isEmpty()) return 0.0;

        double sumaDias = tareasCompletadas.stream()
            .mapToDouble(t -> {
                Duration duration = Duration.between(t.getCreatedAt(), t.getDueDate());
                return duration.toDays();
            })
            .sum();

        return Math.round((sumaDias / tareasCompletadas.size()) * 10.0) / 10.0;
    }
}