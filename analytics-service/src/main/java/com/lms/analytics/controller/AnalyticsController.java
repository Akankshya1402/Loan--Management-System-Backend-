package com.lms.analytics.controller;

import com.lms.analytics.dto.DashboardResponse;
import com.lms.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService service;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return service.getDashboard();
    }
}
