package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/daily-summary")
    public ResponseEntity<Map<String, Object>> getDailySummary(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getDailySummary(authentication.getName()));
    }

    @GetMapping("/average-duration")
    public ResponseEntity<Map<String, Object>> getAverageDuration(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getAverageDuration(authentication.getName()));
    }

    @GetMapping("/staff-performance")
    public ResponseEntity<List<Map<String, Object>>> getStaffPerformance(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getStaffPerformance(authentication.getName()));
    }
}
