package com.example.packageassignment.service;

import com.example.packageassignment.model.Assignment;
import com.example.packageassignment.model.DailyPackageAssignments;
import com.example.packageassignment.repository.DailyPackageAssignmentsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class DPAService {

    private final DailyPackageAssignmentsRepository dailyPackageAssignmentsRepository;

    public DPAService(DailyPackageAssignmentsRepository dailyPackageAssignmentsRepository) {
        this.dailyPackageAssignmentsRepository = dailyPackageAssignmentsRepository;
    }

    public List<DailyPackageAssignments> findAll() {
        return dailyPackageAssignmentsRepository.findAll();
    }

    public void addAssignment(String packageId, String date, boolean forced, Assignment assignment) {
        List<DailyPackageAssignments> dailyAssignmentsList = dailyPackageAssignmentsRepository.findAll();
        DailyPackageAssignments dailyAssignments = null;

        for (DailyPackageAssignments dailyAssignment : dailyAssignmentsList) {
            if (dailyAssignment.getDate().equals(date)) {
                dailyAssignments = dailyAssignment;
                break;
            }
        }

        if (dailyAssignments == null) {
            dailyAssignments = new DailyPackageAssignments(date);
        }

        List<Assignment> assignments = dailyAssignments.getAssignments();
        boolean foundPackageId = false;

        for (Assignment existingAssignment : assignments) {
            if (existingAssignment.getPackageId().equals(packageId)) {
                foundPackageId = true;

                if (forced && existingAssignment.isForced()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Package already forced.");
                } else if (forced && !existingAssignment.isForced()) {
                    assignments.add(assignment);
                }
            }
        }

        if (!foundPackageId) {
            assignments.add(assignment);
        }

        dailyAssignments.setAssignments(assignments);
        dailyPackageAssignmentsRepository.save(dailyAssignments);
    }

    public Assignment createAssignment(String packageId, String driverId, String action, String status,
            int postponedCount, boolean forced) {
        return new Assignment(packageId, driverId, action, status, postponedCount, forced);
    }

    /*
     * public DailyPackageAssignments updateAssignment(String date, String
     * packageId, String action) {
     * DailyPackageAssignments dailyAssignments =
     * dailyPackageAssignmentsRepository.findBydate(date);
     * 
     * if (dailyAssignments == null) {
     * throw new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "No assignments found for the given date.");
     * }
     * 
     * List<Assignment> assignments = dailyAssignments.getAssignments();
     * boolean foundAssignment = false;
     * 
     * for (Assignment assignment : assignments) {
     * if (assignment.getPackageId().equals(packageId) &&
     * "Assigned".equalsIgnoreCase(assignment.getStatus())) {
     * assignment.setAction(action);
     * foundAssignment = true;
     * break;
     * }
     * }
     * 
     * if (!foundAssignment) {
     * throw new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "No matching assignment found for the given package ID and status.");
     * }
     * 
     * dailyAssignments.setAssignments(assignments);
     * return dailyPackageAssignmentsRepository.save(dailyAssignments);
     * }
     */

    public List<DailyPackageAssignments> getSortedAssignmentHistoryByDate() {
        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        assignmentHistory.sort(Comparator.comparing(DailyPackageAssignments::getDate));
        return assignmentHistory;
    }

    public List<DailyPackageAssignments> getSortedAssignmentHistoryByInterval(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }

        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            LocalDate date = LocalDate.parse(assignments.getDate());

            if (date.isEqual(start) || date.isEqual(end) || (date.isAfter(start) && date.isBefore(end))) {
                filteredHistory.add(assignments);
            }
        }

        Collections.sort(filteredHistory, Comparator.comparing(DailyPackageAssignments::getDate));
        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByPackage(String packageId) {
        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());
            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getPackageId().equals(packageId)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }
            if (!filteredAssignments.getAssignments().isEmpty()) {
                filteredHistory.add(filteredAssignments);
            }
        }

        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByDriver(String driverId) {
        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());
            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getDriverId() != null && assignment.getDriverId().equals(driverId)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }
            if (!filteredAssignments.getAssignments().isEmpty()) {
                filteredHistory.add(filteredAssignments);
            }
        }

        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByStatus(String status) {
        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());
            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getStatus() != null && assignment.getStatus().equals(status)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }
            if (!filteredAssignments.getAssignments().isEmpty()) {
                filteredHistory.add(filteredAssignments);
            }
        }

        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByAction(String action) {
        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());
            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getAction() != null && assignment.getAction().equals(action)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }
            if (!filteredAssignments.getAssignments().isEmpty()) {
                filteredHistory.add(filteredAssignments);
            }
        }

        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByStatusAndInterval(String status, String startDate,
            String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }

        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            LocalDate date = LocalDate.parse(assignments.getDate());
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());

            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getStatus() != null && assignment.getStatus().equals(status)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }

            if (!filteredAssignments.getAssignments().isEmpty() &&
                    (date.isEqual(start) || date.isEqual(end) || (date.isAfter(start) && date.isBefore(end)))) {
                filteredHistory.add(filteredAssignments);
            }
        }

        Collections.sort(filteredHistory, Comparator.comparing(DailyPackageAssignments::getDate));
        return filteredHistory;
    }

    public List<DailyPackageAssignments> getAssignmentHistoryByActionAndInterval(String action, String startDate,
            String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }

        List<DailyPackageAssignments> assignmentHistory = dailyPackageAssignmentsRepository.findAll();
        List<DailyPackageAssignments> filteredHistory = new ArrayList<>();

        for (DailyPackageAssignments assignments : assignmentHistory) {
            LocalDate date = LocalDate.parse(assignments.getDate());
            DailyPackageAssignments filteredAssignments = new DailyPackageAssignments(assignments.getDate());

            for (Assignment assignment : assignments.getAssignments()) {
                if (assignment.getAction() != null && assignment.getAction().equals(action)) {
                    filteredAssignments.getAssignments().add(assignment);
                }
            }

            if (!filteredAssignments.getAssignments().isEmpty() &&
                    (date.isEqual(start) || date.isEqual(end) || (date.isAfter(start) && date.isBefore(end)))) {
                filteredHistory.add(filteredAssignments);
            }
        }

        Collections.sort(filteredHistory, Comparator.comparing(DailyPackageAssignments::getDate));
        return filteredHistory;
    }

}