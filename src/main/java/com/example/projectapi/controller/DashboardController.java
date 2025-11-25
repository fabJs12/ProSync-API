package com.example.projectapi.controller;
    
import com.example.projectapi.dto.DashboardStatsDTO;
import com.example.projectapi.model.User;
import com.example.projectapi.service.DashboardService;
import com.example.projectapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public DashboardController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DashboardStatsDTO stats = dashboardService.getStats(user.getId());
        return ResponseEntity.ok(stats);
    }
}