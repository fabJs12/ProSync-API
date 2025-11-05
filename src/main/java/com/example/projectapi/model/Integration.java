package com.example.projectapi.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.projectapi.util.JsonNodeConverter;

// No corregido

@Entity
@Table(name = "integrations", schema = "public")
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Convert(converter = JsonNodeConverter.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public JsonNode getDetails() { return details; }
    public void setDetails(JsonNode details) { this.details = details; }
}