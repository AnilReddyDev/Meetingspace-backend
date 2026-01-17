
package com.meetingspace.controller;

import com.meetingspace.dto.AdminDashboardResponse;
import com.meetingspace.dto.AdminRoomsDashboardResponse;
import com.meetingspace.service.AdminDashboardService;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    // ✅ Explicitly produce JSON and return ResponseEntity so Content-Type and body are set
//    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<AdminDashboardResponse> getRoomSummary() {
//        return ResponseEntity.ok(adminDashboardService.getTodayDashboard());
//    }

    @GetMapping("/rooms")
     public AdminRoomsDashboardResponse getRoomsDashboard(
             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
     ) {
         // Example: 2026-01-16
         return adminDashboardService.getRoomsDashboardForDate(date);
     }

}
