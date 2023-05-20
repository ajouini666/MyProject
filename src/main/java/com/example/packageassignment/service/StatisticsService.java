package com.example.packageassignment.service;

import com.example.packageassignment.model.Assignment;
import com.example.packageassignment.model.Package;
import com.example.packageassignment.model.DailyPackageAssignments;
import com.example.packageassignment.repository.DailyPackageAssignmentsRepository;
import com.example.packageassignment.repository.PackageRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StatisticsService {
    private final DailyPackageAssignmentsRepository dailyPackageAssignmentsRepository;

    @Autowired
    private PackageRepository packageRepository;

    public StatisticsService(DailyPackageAssignmentsRepository dailyPackageAssignmentsRepository) {
        this.dailyPackageAssignmentsRepository = dailyPackageAssignmentsRepository;
    }

    public int getTotalAssignments() {
        int totalAssignments = 0;
        List<DailyPackageAssignments> dailyAssignmentsList = dailyPackageAssignmentsRepository.findAll();
        for (DailyPackageAssignments dailyAssignments : dailyAssignmentsList) {
            totalAssignments += dailyAssignments.getAssignments().size();
        }
        return totalAssignments;
    }

    public int getTotalAssignmentsByDate(String date) {
        DailyPackageAssignments dailyAssignments = dailyPackageAssignmentsRepository.findByDate(date);
        if (dailyAssignments != null) {
            List<Assignment> assignments = dailyAssignments.getAssignments();
            return assignments.size();
        }
        return 0;
    }

    public int getAssignmentsInPast30Days() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        int assignmentsCount = 0;

        for (DailyPackageAssignments dailyAssignments : assignmentsList) {
            LocalDate date = LocalDate.parse(dailyAssignments.getDate(), formatter);
            if (date.isAfter(thirtyDaysAgo) || date.isEqual(thirtyDaysAgo)) {
                assignmentsCount += dailyAssignments.getAssignments().size();
            }
        }

        return assignmentsCount;
    }

    public int getAssignmentsInPastXDays(int x) {
        LocalDate today = LocalDate.now();
        LocalDate xDaysAgo = today.minusDays(x);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        int assignmentsCount = 0;

        for (DailyPackageAssignments dailyAssignments : assignmentsList) {
            LocalDate date = LocalDate.parse(dailyAssignments.getDate(), formatter);
            if (date.isAfter(xDaysAgo) || date.isEqual(xDaysAgo)) {
                assignmentsCount += dailyAssignments.getAssignments().size();
            }
        }

        return assignmentsCount;
    }

    public int getAssignmentsByDriver(String date, String driverId) {
        DailyPackageAssignments dailyAssignments = dailyPackageAssignmentsRepository.findByDate(date);
        if (dailyAssignments != null) {
            List<Assignment> assignments = dailyAssignments.getAssignments();
            int count = 0;
            for (Assignment assignment : assignments) {
                if (assignment.getDriverId().equals(driverId)) {
                    count++;
                }
            }
            return count;
        }
        return 0;
    }

    public Map<String, Integer> getAssignmentsInPastXDaysMap(int x) {
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        Map<String, Integer> result = new HashMap<>();

        // Create a list of dates for the past x days
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date);
        }

        // Iterate over the dates and check if an assignment exists for each date
        for (LocalDate date : dates) {
            String formattedDate = date.format(formatter);
            int assignmentsCount = 0;

            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                LocalDate assignmentDate = LocalDate.parse(dailyAssignments.getDate(), formatter);
                if (assignmentDate.isEqual(date)) {
                    assignmentsCount += dailyAssignments.getAssignments().size();
                }
            }

            result.put(formattedDate, assignmentsCount);
        }

        return result;
    }

    public Map<String, Integer> getAssignmentsByStatusInPastXDaysMap(int x, String status) {
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        Map<String, Integer> result = new HashMap<>();

        // Create a list of dates for the past x days
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date);
        }

        // Iterate over the dates and check if an assignment exists for each date
        for (LocalDate date : dates) {
            String formattedDate = date.format(formatter);
            int assignmentsCount = 0;

            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                LocalDate assignmentDate = LocalDate.parse(dailyAssignments.getDate(), formatter);
                if (assignmentDate.isEqual(date)) {
                    for (Assignment assignment : dailyAssignments.getAssignments()) {
                        if (assignment.getStatus().equals(status)) {
                            assignmentsCount++;
                        }
                    }
                }
            }

            result.put(formattedDate, assignmentsCount);
        }

        return result;
    }

    public Map<String, Integer> getAssignmentsByActionInPastXDaysMap(int x, String action) {
        LocalDate today = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        Map<String, Integer> result = new HashMap<>();

        // Create a list of dates for the past x days
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date);
        }

        // Iterate over the dates and check if an assignment exists for each date
        for (LocalDate date : dates) {
            String formattedDate = date.format(formatter);
            int assignmentsCount = 0;

            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                LocalDate assignmentDate = LocalDate.parse(dailyAssignments.getDate(), formatter);
                if (assignmentDate.isEqual(date)) {
                    for (Assignment assignment : dailyAssignments.getAssignments()) {
                        if (assignment.getAction().equals(action)) {
                            assignmentsCount++;
                        }
                    }
                }
            }

            result.put(formattedDate, assignmentsCount);
        }

        return result;
    }

    public List<String> getPackageIdsByStatusInPastXDays(int x, String status) {
        LocalDate today = LocalDate.now();

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        List<String> packageIds = new ArrayList<>();

        // Create a list of dates for the past x days
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());
        }

        // Iterate over the dates and collect package IDs for each assignment
        for (String date : dates) {
            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                if (dailyAssignments.getDate().equals(date)) {
                    for (Assignment assignment : dailyAssignments.getAssignments()) {
                        if (assignment.getStatus().equals(status)) {
                            packageIds.add(assignment.getPackageId());
                        }
                    }
                }
            }
        }

        return packageIds;
    }

    public Map<String, Integer> getPackageCountByStateInPastXDays(int x, String status) {
        List<String> packageIds = getPackageIdsByStatusInPastXDays(x, status);
        Map<String, Integer> stateCounts = new HashMap<>();

        for (String packageId : packageIds) {
            ObjectId pid = new ObjectId(packageId);
            Optional<Package> packageObj = packageRepository.findById(pid);
            packageObj.ifPresent(pkg -> {
                String state = pkg.getDeliveryLocation().getState();
                stateCounts.put(state, stateCounts.getOrDefault(state, 0) + 1);
            });
        }

        return stateCounts;
    }

    public List<String> getDriverIdsByActionInPastXDays(int x, String action) {
        LocalDate today = LocalDate.now();

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        List<String> driverIds = new ArrayList<>();

        // Create a list of dates for the past x days
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());
        }

        // Iterate over the dates and collect driver IDs for each assignment
        for (String date : dates) {
            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                if (dailyAssignments.getDate().equals(date)) {
                    for (Assignment assignment : dailyAssignments.getAssignments()) {
                        if (assignment.getAction() != null && assignment.getAction().equals(action)) {
                            driverIds.add(assignment.getDriverId());
                        }
                    }
                }
            }
        }

        return driverIds;
    }

    public Map<String, Integer> getDriverIdCountsByActionInPastXDays(int x, String action) {
        List<String> driverIds = getDriverIdsByActionInPastXDays(x, action);

        // Create a map to store driver ID counts
        Map<String, Integer> driverIdCounts = new HashMap<>();

        // Count the occurrences of each driver ID
        for (String driverId : driverIds) {
            driverIdCounts.put(driverId, driverIdCounts.getOrDefault(driverId, 0) + 1);
        }

        return driverIdCounts;
    }

    public List<String> getDriverIdsByStatusInPastXDays(int x) {
        LocalDate today = LocalDate.now();

        List<DailyPackageAssignments> assignmentsList = dailyPackageAssignmentsRepository.findAll();
        List<String> driverIds = new ArrayList<>();

        // Create a list of dates for the past x days
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            LocalDate date = today.minusDays(i);
            dates.add(date.toString());
        }

        // Iterate over the dates and collect driver IDs for each assignment
        for (String date : dates) {
            for (DailyPackageAssignments dailyAssignments : assignmentsList) {
                if (dailyAssignments.getDate().equals(date)) {
                    for (Assignment assignment : dailyAssignments.getAssignments()) {
                        if (assignment.getStatus() != null && assignment.getStatus().equals("Assigned")) {
                            driverIds.add(assignment.getDriverId());
                        }
                    }
                }
            }
        }

        return driverIds;
    }

    public Map<String, Integer> getDriverIdCountsByStatusInPastXDays(int x) {
        List<String> driverIds = getDriverIdsByStatusInPastXDays(x);

        // Create a map to store driver ID counts
        Map<String, Integer> driverIdCounts = new HashMap<>();

        // Count the occurrences of each driver ID
        for (String driverId : driverIds) {
            driverIdCounts.put(driverId, driverIdCounts.getOrDefault(driverId, 0) + 1);
        }

        return driverIdCounts;
    }

    public Map<String, Double> getDriverAssignmentDeliveryRatiosInPastXDays(int x) {
        Map<String, Integer> deliveredCounts = getDriverIdCountsByStatusInPastXDays(x);
        Map<String, Integer> assignedCounts = getDriverIdCountsByActionInPastXDays(x, "Delivered");

        Map<String, Double> ratios = new HashMap<>();

        // Iterate over the assigned counts
        for (Map.Entry<String, Integer> entry : assignedCounts.entrySet()) {
            String driverId = entry.getKey();
            int assignedCount = entry.getValue();

            // Get the delivered count for the driver (or 0 if not present in
            // deliveredCounts)
            int deliveredCount = deliveredCounts.getOrDefault(driverId, 0);

            // Calculate the ratio as a percentage
            double ratio = deliveredCount > 0 ? (double) assignedCount / deliveredCount * 100 : 0;

            // Store the ratio in the map
            ratios.put(driverId, ratio);
        }

        return ratios;
    }

}