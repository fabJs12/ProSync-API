package com.example.projectapi.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateTaskDTO {
    private String title;
    private String description;
    private OffsetDateTime dueDate;
    private Integer boardId;
    private Integer estadoId;
    private Integer responsableId; // Puede ser null
}