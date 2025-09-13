package com.FishOn.FishOn.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}