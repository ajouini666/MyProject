package com.example.packageassignment.service;

import org.springframework.stereotype.Service;

import com.example.packageassignment.model.DriverSchedule;
import com.example.packageassignment.model.DriverWorkDays;
import com.example.packageassignment.model.WorkDay;
import com.example.packageassignment.model.WorkingHours;
import com.example.packageassignment.repository.DriverScheduleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.packageassignment.Config.NotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DriverWFMService {
    private final DriverScheduleRepository driverScheduleRepository;

    public DriverWFMService(DriverScheduleRepository driverScheduleRepository) {
        this.driverScheduleRepository = driverScheduleRepository;
    }

    public Boolean isDriverOnDuty(String driverObjectId) {
        DriverSchedule driverSchedule = driverScheduleRepository.findByDriverSchedulesDriverId(driverObjectId);

        if (driverSchedule == null) {
            return false;
        }

        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

        for (DriverWorkDays driver : driverSchedule.getDriverSchedules()) {
            if (driver.getDriverId().equals(driverObjectId)) {
                for (WorkDay workDay : driver.getWorkDays()) {
                    if (workDayMatchesCurrentDay(workDay, currentTime)) {
                        return workingHoursContainCurrentTime(workDay.getWorkingHours(), currentTime);
                    }
                }
                break;
            }
        }

        return false;
    }

    private boolean workDayMatchesCurrentDay(WorkDay workDay, LocalDateTime currentTime) {
        String currentDayOfWeek = currentTime.getDayOfWeek().name();
        return workDay.getDayOfWeek().equalsIgnoreCase(currentDayOfWeek);
    }

    private boolean workingHoursContainCurrentTime(WorkingHours workingHours, LocalDateTime currentTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneOffset.UTC);
        LocalTime startTime = LocalTime.parse(workingHours.getStart(), formatter);
        LocalTime endTime = LocalTime.parse(workingHours.getEnd(), formatter);
        LocalTime currentTimeWithoutSeconds = currentTime.toLocalTime().withSecond(0);

        return !currentTimeWithoutSeconds.isBefore(startTime) && !currentTimeWithoutSeconds.isAfter(endTime);
    }

    public List<WorkDay> getDriverWorkDays(String driverId, int week) throws NotFoundException {
        DriverSchedule driverSchedule = driverScheduleRepository.findByWeekAndDriverSchedulesDriverId(week, driverId);
        if (driverSchedule == null) {
            throw new NotFoundException(
                    "No driverSchedule with the DriverId : " + driverId + "in week : " + week + " were found");
        }

        List<DriverWorkDays> driverSchedules = driverSchedule.getDriverSchedules();
        for (DriverWorkDays driverWorkDays : driverSchedules) {
            if (driverWorkDays.getDriverId().equals(driverId)) {
                return driverWorkDays.getWorkDays();
            }
        }

        throw new NotFoundException(
                "No driverSchedule with the DriverId : " + driverId + "in week : " + week + " were found");
    }

    public List<DriverSchedule> getAllDriverSchedulesForWeek(int week) {
        List<DriverSchedule> driverSchedules = driverScheduleRepository.findByWeek(week);
        if (driverSchedules.isEmpty()) {
            throw new NotFoundException("No driver schedules found for week " + week
                    + " ,The Schedules could've not been created yet for that week.");
        }
        return driverSchedules;
    }

    public List<DriverWorkDays> getDriverSchedulesForWeekAndDay(int week, String day) {
        List<DriverSchedule> schedules = driverScheduleRepository.findAll();
        List<DriverWorkDays> filteredSchedules = new ArrayList<>();

        for (DriverSchedule schedule : schedules) {
            if (schedule.getWeek() == week) {
                List<DriverWorkDays> driverSchedules = schedule.getDriverSchedules();
                for (DriverWorkDays driverWorkDays : driverSchedules) {
                    List<WorkDay> workDays = driverWorkDays.getWorkDays();
                    for (WorkDay workDay : workDays) {
                        if (workDay.getDayOfWeek().equalsIgnoreCase(day)) {
                            // Add only the schedules for the specified day
                            DriverWorkDays filteredSchedule = new DriverWorkDays(driverWorkDays.getDriverId(),
                                    Collections.singletonList(workDay));
                            filteredSchedules.add(filteredSchedule);
                            break;
                        }
                    }
                }
            }
        }
        return filteredSchedules;
    }

    public List<DriverWorkDays> getDriverScheduleForWeekDayAndDriver(int week, String day, String driverId) {
        List<DriverSchedule> schedules = driverScheduleRepository.findAll();
        List<DriverWorkDays> driverWorkDaysList = new ArrayList<>();

        for (DriverSchedule schedule : schedules) {
            if (schedule.getWeek() == week) {
                List<DriverWorkDays> driverSchedules = schedule.getDriverSchedules();
                for (DriverWorkDays driverWorkDays : driverSchedules) {
                    if (driverWorkDays.getDriverId().equals(driverId)) {
                        List<WorkDay> workDays = driverWorkDays.getWorkDays();
                        for (WorkDay workDay : workDays) {
                            if (workDay.getDayOfWeek().equalsIgnoreCase(day)) {
                                driverWorkDaysList.add(new DriverWorkDays(driverWorkDays.getDriverId(),
                                        Collections.singletonList(workDay)));
                            }
                        }
                    }
                }
            }
        }

        return driverWorkDaysList;
    }

    public List<String> getActiveDriverIds() {
        // Get the current week number
        int currentWeek = getCurrentWeekNumber();

        // Retrieve the driver schedules for the current week
        List<DriverSchedule> schedules = driverScheduleRepository.findByWeek(currentWeek);

        // Collect the driver IDs from the schedules
        return schedules.stream()
                .map(DriverSchedule::getDriverSchedules)
                .flatMap(Collection::stream)
                .map(DriverWorkDays::getDriverId)
                .collect(Collectors.toList());
    }

    private int getCurrentWeekNumber() {
        // Get the current UTC date and time
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);

        // Determine the week fields based on the locale
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Get the week number of the current date and time
        return currentDateTime.get(weekFields.weekOfWeekBasedYear());
    }

    public List<String> getActiveDriverIdsForDay() {
        // Get the current week number
        int currentWeek = getCurrentWeekNumber();

        // Get the current day of the week
        String currentDayOfWeek = getCurrentDayOfWeek();

        // Retrieve the driver schedules for the current week
        List<DriverSchedule> schedules = driverScheduleRepository.findByWeek(currentWeek);

        // Collect the driver IDs from the schedules for the current day
        return schedules.stream()
                .map(DriverSchedule::getDriverSchedules)
                .flatMap(Collection::stream)
                .filter(driverWorkDays -> driverWorkDays.getWorkDays().stream()
                        .anyMatch(workDay -> workDay.getDayOfWeek().equalsIgnoreCase(currentDayOfWeek)))
                .map(DriverWorkDays::getDriverId)
                .collect(Collectors.toList());
    }

    private String getCurrentDayOfWeek() {
        // Get the current UTC date and time
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);

        // Get the day of the week as a string (e.g., "Monday", "Tuesday", etc.)
        return currentDateTime.getDayOfWeek().name();
    }

    public List<String> getActiveDriverIdsForCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();

        return driverScheduleRepository.findAll().stream()
                .filter(driverSchedule -> hasWorkingHours(driverSchedule, currentDayOfWeek, currentTime))
                .map(DriverSchedule::getDriverId)
                .collect(Collectors.toList());
    }

    private boolean hasWorkingHours(DriverSchedule driverSchedule, DayOfWeek dayOfWeek, LocalTime currentTime) {
        return driverSchedule.getDriverSchedules().stream()
                .filter(driverWorkDays -> driverWorkDays.getDriverId().equals(driverSchedule.getDriverId()))
                .flatMap(driverWorkDays -> driverWorkDays.getWorkDays().stream())
                .filter(workDay -> workDay.getDayOfWeek().equalsIgnoreCase(dayOfWeek.toString()))
                .anyMatch(workDay -> isWithinWorkingHours(workDay.getWorkingHours(), currentTime));
    }

    private boolean isWithinWorkingHours(WorkingHours workingHours, LocalTime currentTime) {
        LocalTime start = LocalTime.parse(workingHours.getStart());
        LocalTime end = LocalTime.parse(workingHours.getEnd());

        if (start.isBefore(end)) {
            // Working hours are within the same day
            return currentTime.isAfter(start) && currentTime.isBefore(end);
        } else {
            // Working hours span across two different days (e.g., "11:59 PM" to "12:30 AM")
            return currentTime.isAfter(start) || currentTime.isBefore(end);
        }
    }

    public void updateDriverSchedule(String driverId, int week, String input) {
        List<WorkDay> workDays = parseWorkDays(input);
        // Check if the week exists in the collection
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            // Week exists, search for the driverId in driverSchedules
            List<DriverWorkDays> driverSchedules = driverSchedule.getDriverSchedules();

            for (DriverWorkDays driverWorkDays : driverSchedules) {
                if (driverWorkDays.getDriverId().equals(driverId)) {
                    // DriverId found, update workDays
                    driverWorkDays.setWorkDays(workDays);
                    driverScheduleRepository.save(driverSchedule); // Save the changes made to the driverSchedule object
                    return;
                }
            }

            // DriverId not found, create a new DriverWorkDays object and add it to
            // driverSchedules
            DriverWorkDays newDriverWorkDays = new DriverWorkDays(driverId, workDays);
            driverSchedules.add(newDriverWorkDays);
            driverScheduleRepository.save(driverSchedule); // Save the changes made to the driverSchedule object
        } else {
            // Week does not exist, create a new DriverSchedule with a single DriverWorkDays
            // object
            List<DriverWorkDays> driverSchedules = new ArrayList<>();
            DriverWorkDays newDriverWorkDays = new DriverWorkDays(driverId, workDays);
            driverSchedules.add(newDriverWorkDays);

            driverSchedule = new DriverSchedule();
            driverSchedule.setWeek(week);
            driverSchedule.setDriverSchedules(driverSchedules);
            driverScheduleRepository.save(driverSchedule); // Save the new driverSchedule object
        }
    }

    // takes input in the form of "Monday 09:00AM 05:00PM;Tuesday 10:00AM
    // 06:00PM;Wednesday 08:00AM 04:00PM"
    public static List<WorkDay> parseWorkDays(String workDaysString) throws IllegalArgumentException {
        List<WorkDay> workDays = new ArrayList<>();

        String[] dayStrings = workDaysString.split(";");
        for (String dayString : dayStrings) {
            String[] parts = dayString.split("\\s+");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid work day format: " + dayString);
            }

            String dayOfWeek = parts[0];
            String startTime = parts[1];
            String endTime = parts[2];

            if (!isValidTime(startTime) || !isValidTime(endTime)) {
                throw new IllegalArgumentException("Invalid time format: " + dayString);
            }

            // Add a space between the time and format
            startTime = fixTimeFormat(startTime);
            endTime = fixTimeFormat(endTime);

            WorkDay workDay = new WorkDay(dayOfWeek, new WorkingHours(startTime, endTime));
            workDays.add(workDay);
        }

        return workDays;
    }

    public static boolean isValidTime(String time) {
        // Check if the time is in the format "hh:mmXX" where XX is AM or PM
        Pattern pattern = Pattern.compile("^(0?[1-9]|1[0-2]):[0-5][0-9][ ]*(AM|PM)$");
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }

    public static String fixTimeFormat(String time) {
        StringBuilder fixedTime = new StringBuilder(time);
        fixedTime.insert(time.length() - 2, ' ');
        return fixedTime.toString();
    }

    public void updateDriverDailySchedule(String driverId, int week, String dayOfWeek, JsonNode workingHours) {
        WorkingHours hours = new WorkingHours(
                workingHours.get("start").asText(),
                workingHours.get("end").asText());

        // Check if the week exists in the collection
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            // Week exists, search for the driverId in driverSchedules
            List<DriverWorkDays> driverSchedules = driverSchedule.getDriverSchedules();

            for (DriverWorkDays driverWorkDays : driverSchedules) {
                if (driverWorkDays.getDriverId().equals(driverId)) {
                    // DriverId found, update workDays
                    List<WorkDay> workDays = driverWorkDays.getWorkDays();
                    for (WorkDay workDay : workDays) {
                        if (workDay.getDayOfWeek().equals(dayOfWeek)) {
                            workDay.setWorkingHours(hours);
                            driverScheduleRepository.save(driverSchedule);
                            return;
                        }
                    }

                    // DayOfWeek not found, create a new WorkDay object and add it to workDays
                    WorkDay newWorkDay = new WorkDay(dayOfWeek, hours);
                    workDays.add(newWorkDay);
                    driverScheduleRepository.save(driverSchedule);
                    return;
                }
            }

            // DriverId not found, create a new DriverWorkDays object and add it to
            // driverSchedules
            List<WorkDay> workDays = new ArrayList<>();
            WorkDay newWorkDay = new WorkDay(dayOfWeek, hours);
            workDays.add(newWorkDay);
            DriverWorkDays newDriverWorkDays = new DriverWorkDays(driverId, workDays);
            driverSchedules.add(newDriverWorkDays);
            driverScheduleRepository.save(driverSchedule);
        } else {
            // Week does not exist, create a new DriverSchedule with a single DriverWorkDays
            // object
            List<DriverWorkDays> driverSchedules = new ArrayList<>();
            List<WorkDay> workDays = new ArrayList<>();
            WorkDay newWorkDay = new WorkDay(dayOfWeek, hours);
            workDays.add(newWorkDay);
            DriverWorkDays newDriverWorkDays = new DriverWorkDays(driverId, workDays);
            driverSchedules.add(newDriverWorkDays);
            driverSchedule = new DriverSchedule(week, driverSchedules);
            driverScheduleRepository.save(driverSchedule);
        }
    }

    public void deleteDriverWorkDay(String driverId, int week, String dayOfWeek) throws NotFoundException {
        DriverSchedule driverSchedule = driverScheduleRepository.findByWeekAndDriverSchedulesDriverId(week, driverId);
        if (driverSchedule == null) {
            throw new NotFoundException(
                    "No driverSchedule with the DriverId: " + driverId + " in week: " + week + " were found");
        }

        List<DriverWorkDays> driverSchedules = driverSchedule.getDriverSchedules();
        for (DriverWorkDays driverWorkDays : driverSchedules) {
            if (driverWorkDays.getDriverId().equals(driverId)) {
                List<WorkDay> workDays = driverWorkDays.getWorkDays();
                for (WorkDay workDay : workDays) {
                    if (workDay.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                        workDays.remove(workDay);
                        driverScheduleRepository.save(driverSchedule);
                        return;
                    }
                }
            }
        }

        throw new NotFoundException(
                "No WorkDay found for driverId: " + driverId + ", week: " + week + ", and dayOfWeek: " + dayOfWeek);
    }

    public void deleteDriverSchedule(String driverId, int week) throws NotFoundException {
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            List<DriverWorkDays> driverSchedules = driverSchedule.getDriverSchedules();

            // Find the driver schedule for the specified driver ID
            for (DriverWorkDays driverWorkDays : driverSchedules) {
                if (driverWorkDays.getDriverId().equals(driverId)) {
                    driverSchedules.remove(driverWorkDays);
                    driverScheduleRepository.save(driverSchedule);
                    return;
                }
            }
        }

        throw new NotFoundException("No driver schedule found for driver ID: " + driverId + " and week: " + week);
    }

    public void deleteDriverScheduleByWeek(int week) throws NotFoundException {
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            driverScheduleRepository.delete(driverSchedule);
        } else {
            throw new NotFoundException("No driver schedule found for week: " + week);
        }
    }

    public double calculateWeeklyHours(String driverId, int week) {
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            double totalHours = 0;

            for (DriverWorkDays driverWorkDays : driverSchedule.getDriverSchedules()) {
                if (driverWorkDays.getDriverId().equals(driverId)) {
                    for (WorkDay workDay : driverWorkDays.getWorkDays()) {
                        if (workDay.getWorkingHours() != null) {
                            String start = workDay.getWorkingHours().getStart();
                            String end = workDay.getWorkingHours().getEnd();
                            LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern("hh:mm a"));
                            LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern("hh:mm a"));
                            long hours = ChronoUnit.HOURS.between(startTime, endTime);
                            totalHours += hours;
                        }
                    }
                    return totalHours; // Return the total hours once found
                }
            }
        }

        return 0; // Return 0 if the driver schedule is not found or if no matching driverId is
                  // found
    }

    public double calculateDailyHours(String driverId, int week, String dayOfWeek) {
        DriverSchedule driverSchedule = driverScheduleRepository.findAllByWeek(week);

        if (driverSchedule != null) {
            for (DriverWorkDays driverWorkDays : driverSchedule.getDriverSchedules()) {
                if (driverWorkDays.getDriverId().equals(driverId)) {
                    for (WorkDay workDay : driverWorkDays.getWorkDays()) {
                        if (workDay.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                            WorkingHours workingHours = workDay.getWorkingHours();
                            if (workingHours != null) {
                                String start = workingHours.getStart();
                                String end = workingHours.getEnd();
                                LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern("hh:mm a"));
                                LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern("hh:mm a"));
                                return ChronoUnit.HOURS.between(startTime, endTime);
                            }
                        }
                    }
                    break;
                }
            }
        }

        return 0; // Return 0 if the driver schedule is not found or if no matching driverId or
                  // dayOfWeek is found
    }

}
