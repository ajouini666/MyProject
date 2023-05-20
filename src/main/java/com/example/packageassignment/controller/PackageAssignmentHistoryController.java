package com.example.packageassignment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.model.DailyPackageAssignments;
import com.example.packageassignment.service.DPAService;

@RestController
@RequestMapping("/api/History")
public class PackageAssignmentHistoryController {
    private final DPAService DPAService;

    public PackageAssignmentHistoryController(DPAService DPAService) {
        this.DPAService = DPAService;
    }

    @GetMapping("/history/sort-by-date")
    public List<DailyPackageAssignments> getAssignmentHistorySortedByDate() {
        return DPAService.getSortedAssignmentHistoryByDate();
    }

    @GetMapping("/sort-by-interval")
    public List<DailyPackageAssignments> getAssignmentHistorySortedByInterval(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return DPAService.getSortedAssignmentHistoryByInterval(startDate, endDate);
    }

    @GetMapping("/package/{packageId}")
    public List<DailyPackageAssignments> getAssignmentHistoryByPackage(@PathVariable String packageId) {
        return DPAService.getAssignmentHistoryByPackage(packageId);
    }

    @GetMapping("/driver/{driverId}")
    public List<DailyPackageAssignments> getAssignmentHistoryByDriver(@PathVariable String driverId) {
        return DPAService.getAssignmentHistoryByDriver(driverId);
    }

    @GetMapping("/status/{status}")
    public List<DailyPackageAssignments> getAssignmentHistoryByStatus(@PathVariable String status) {
        return DPAService.getAssignmentHistoryByStatus(status);
    }

    @GetMapping("/status/filter-by-status-and-interval")
    public List<DailyPackageAssignments> getAssignmentHistoryByStatusAndInterval(
            @RequestParam String status,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return DPAService.getAssignmentHistoryByStatusAndInterval(status, startDate, endDate);
    }

    @GetMapping("/action/{action}")
    public List<DailyPackageAssignments> getAssignmentHistoryByAction(@PathVariable String action) {
        return DPAService.getAssignmentHistoryByStatus(action);
    }

    @GetMapping("/action/filter-by-action-and-interval")
    public List<DailyPackageAssignments> getAssignmentHistoryByActionAndInterval(
            @RequestParam String action,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return DPAService.getAssignmentHistoryByActionAndInterval(action, startDate, endDate);
    }
}
