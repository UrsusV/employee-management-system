package com.example.employeemanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());

        Map<String, Object> details = new HashMap<>();
        details.put("application", checkApplication());
        details.put("database", checkDatabase());

        health.put("components", details);
        return ResponseEntity.ok(health);
    }

    @GetMapping("/live")
    public ResponseEntity<Map<String, String>> liveness() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ALIVE");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        boolean databaseReady = checkDatabase().get("status").equals("UP");

        response.put("ready", databaseReady);
        response.put("timestamp", LocalDateTime.now());
        response.put("checks", Map.of(
                "database", databaseReady ? "READY" : "NOT_READY"
        ));

        return ResponseEntity.ok(response);
    }

    private Map<String, String> checkApplication() {
        Map<String, String> appHealth = new HashMap<>();
        appHealth.put("status", "UP");
        appHealth.put("name", "Employee Management System");
        appHealth.put("version", "1.0.0");
        return appHealth;
    }

    private Map<String, String> checkDatabase() {
        Map<String, String> dbHealth = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                dbHealth.put("status", "UP");
                dbHealth.put("database", connection.getMetaData().getDatabaseProductName());
                dbHealth.put("version", connection.getMetaData().getDatabaseProductVersion());
            } else {
                dbHealth.put("status", "DOWN");
                dbHealth.put("error", "Connection validation failed");
            }
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
        }
        return dbHealth;
    }
}