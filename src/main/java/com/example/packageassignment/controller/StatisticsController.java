package com.example.packageassignment.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.service.StatisticsService;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/totalAssignments")
    public int getTotalAssignments() {
        return statisticsService.getTotalAssignments();
    }

    @GetMapping("/assignmentsByDriver")
    public int getAssignmentsByDriver(
            @RequestParam("date") String date,
            @RequestParam("driverId") String driverId) {
        return statisticsService.getAssignmentsByDriver(date, driverId);
    }

    @GetMapping("/past-days")
    public Map<String, Integer> getAssignmentsInPastXDays(@RequestParam("x") int x) {
        return statisticsService.getAssignmentsInPastXDaysMap(x);
    }

    @GetMapping("/Status/{status}/past-days")
    public Map<String, Integer> getAssignmentsStatusInPastXDaysMap(@PathVariable String status,
            @RequestParam("x") int x) {
        return statisticsService.getAssignmentsByStatusInPastXDaysMap(x, status);
    }

    @GetMapping("/Action/{action}/past-days")
    public Map<String, Integer> getAssignmentsActionInPastXDaysMap(@PathVariable String action,
            @RequestParam("x") int x) {
        return statisticsService.getAssignmentsByActionInPastXDaysMap(x, action);
    }

    @GetMapping("/State/{status}/{x}")
    public Map<String, Integer> getPackageCountByStateInPastXDays(@PathVariable String status,
            @PathVariable int x) {
        return statisticsService.getPackageCountByStateInPastXDays(x, status);
    }

    @GetMapping("/Driver/Action/{action}/{x}")
    public Map<String, Integer> getDriverIdCountsByActionInPastXDays(@PathVariable String action,
            @PathVariable int x) {
        return statisticsService.getDriverIdCountsByActionInPastXDays(x, action);
    }

    @GetMapping("/Driver/Assigned/{x}")
    public Map<String, Integer> getDriverIdCountsByStatusInPastXDays(
            @PathVariable int x) {
        return statisticsService.getDriverIdCountsByStatusInPastXDays(x);
    }

    @GetMapping("/Driver/DeliveryRatio/{x}")
    public Map<String, Double> getDriverAssignmentDeliveryRatiosInPastXDays(
            @PathVariable int x) {
        return statisticsService.getDriverAssignmentDeliveryRatiosInPastXDays(x);
    }

}

// Implement other statistics endpoints

// Add more endpoints to retrieve statistics based on your requirements
