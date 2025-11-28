package com.example.projectapi.dto;

import lombok.Data;

@Data
public class CreateUserProjectDTO {
    private Integer userId;
    private Integer projectId;
    private Integer rolId;
}