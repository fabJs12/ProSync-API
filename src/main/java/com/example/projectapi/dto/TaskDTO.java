package com.example.projectapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Integer id;
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    private Integer boardId;
    private String boardName;
    private Integer estadoId;
    private String estadoNombre;
    private Integer responsableId;
    private String responsableUsername;
    private Integer projectId;
    private String projectName;
    private OffsetDateTime createdAt;
}