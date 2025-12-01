package com.example.projectapi.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class ProjectDetailDTO {
    private Integer id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;

    // Aquí está la magia: Una lista simple de miembros, NO la entidad UserProject compleja
    private List<MemberDTO> miembros;

    @Data
    public static class MemberDTO {
        private Integer userId;
        private String username;
        private String email;
        private String rol; // Incluimos el rol porque es útil
    }
}