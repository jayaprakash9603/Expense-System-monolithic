package com.jaya.analyticsservice.controller;

import com.jaya.analyticsservice.dto.ApplicationOverviewDTO;
import com.jaya.analyticsservice.service.AnalyticsOverviewService;
import com.jaya.userservice.modal.User;
import com.jaya.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsOverviewService analyticsOverviewService;
    private final UserService userService;

    @GetMapping("/overview")
    public ResponseEntity<ApplicationOverviewDTO> getApplicationOverview(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "targetId", required = false) Integer targetId) {

        User user = userService.getUserProfile(jwt);
        ApplicationOverviewDTO overview = analyticsOverviewService.getOverview(user.getId());
        return ResponseEntity.ok(overview);
    }
}
