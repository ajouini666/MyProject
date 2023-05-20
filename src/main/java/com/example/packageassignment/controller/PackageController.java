package com.example.packageassignment.controller;

import com.example.packageassignment.model.Assignment;
import com.example.packageassignment.model.DeliveryLocation;
import com.example.packageassignment.model.Driver;
import com.example.packageassignment.model.DriverSchedule;
import com.example.packageassignment.model.DriverWorkDays;
import com.example.packageassignment.model.Package;
import com.example.packageassignment.model.WorkDay;
import com.example.packageassignment.repository.DriverRepository;
import com.example.packageassignment.repository.PackageRepository;
import com.example.packageassignment.service.DPAService;
import com.example.packageassignment.service.DriverPackageAssignmentService;
import com.example.packageassignment.service.DriverWFMService;
import com.example.packageassignment.service.PackageService;
import com.example.packageassignment.service.SchedulingPropertiesService;
import com.fasterxml.jackson.databind.JsonNode;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.support.CronTrigger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@RestController
@RequestMapping("/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DPAService DPAService;

    @Autowired
    private DriverPackageAssignmentService driverPackageAssignmentService;

    @Autowired
    private DriverWFMService DriverWFMService;

    @Autowired
    private SchedulingPropertiesService schedulingPropertiesService;

    private ScheduledTaskRegistrar taskRegistrar;

    private Trigger trigger;
    private ThreadPoolTaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    private Runnable task;

    // New Fields

    public Runnable getRunnable() {
        return this::assignPackagesAutomatically;
    }

    public CronTrigger getTrigger() {
        String cronExpression = schedulingPropertiesService.getCronExpression();
        return new CronTrigger(cronExpression);
    }

    public void setTaskRegistrar(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public void setTaskScheduler(ThreadPoolTaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @PostMapping("/restart-task")
    public ResponseEntity<String> restartTask() {
        try {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false); // Cancel the existing task
            }

            // Schedule a new task with the updated trigger
            scheduledFuture = taskScheduler.schedule(task, getTrigger());

            return ResponseEntity.ok("Task restarted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to restart the task: " + e.getMessage());
        }
    }

    @PutMapping("/toggle-scheduling")
    public ResponseEntity<String> toggleScheduling(@RequestParam("enabled") boolean enabled) {
        schedulingPropertiesService.updateScheduleEnabled(enabled);
        if (enabled) {
            if (scheduledFuture == null || scheduledFuture.isCancelled()) {
                // Start the task if scheduling is enabled and there is no active task
                scheduledFuture = taskScheduler.schedule(task, getTrigger());
            }
        } else {
            // Cancel the task if scheduling is disabled
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
        }

        return ResponseEntity.ok("Scheduling is now " + (enabled ? "enabled" : "disabled") + ".");
    }

    @PutMapping("/schedule/toggle")
    public ResponseEntity<String> enableDisableSchedule(@RequestParam boolean enabled) {
        schedulingPropertiesService.updateScheduleEnabled(enabled);
        return ResponseEntity.ok("Scheduling " + (enabled ? "enabled" : "disabled") + ".");
    }

    @PutMapping("/schedule/single")
    public ResponseEntity<String> setSingleTime(@RequestParam String time) {
        String userCronExpression = convertToCronExpression(time);

        if (userCronExpression == null) {
            return ResponseEntity.badRequest().body("Invalid time format. Please provide time in 'HH:mm' format.");
        }

        schedulingPropertiesService.updateDailySchedule(userCronExpression);
        return ResponseEntity.ok("Cron expression set to " + time + ".");

    }

    @PutMapping("/schedule/weekly")
    public ResponseEntity<String> setWeeklyTime(@RequestParam String time,
            @RequestParam String startDayOfWeek,
            @RequestParam String endDayOfWeek) {
        String userCronExpression = convertToCronExpression(time, startDayOfWeek, endDayOfWeek);

        if (userCronExpression == null) {
            return ResponseEntity.badRequest().body(
                    "Invalid input format. Please provide time in 'HH:mm' format and days of the week in 'Monday', 'Tuesday', etc. format.");
        }

        schedulingPropertiesService.updateDailySchedule(userCronExpression);
        return ResponseEntity.ok("Cron expression set to [" + time + "] Week internval is [" + startDayOfWeek + "-"
                + endDayOfWeek + "] .");

    }

    @PutMapping("/schedule/recurring")
    public ResponseEntity<String> setRecurringTime(@RequestParam String interval,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String startDayOfWeek,
            @RequestParam String endDayOfWeek) {
        String userCronExpression = convertToCronExpression(interval, startTime, endTime, startDayOfWeek, endDayOfWeek);

        if (userCronExpression == null) {
            return ResponseEntity.badRequest().body(
                    "Invalid input format. Please provide interval, start time, end time, and days of the week in the correct format.");
        }

        schedulingPropertiesService.updateDailySchedule(userCronExpression);
        return ResponseEntity.ok("Cron expression set to every [" + interval + "] with Daily Start and end time of ["
                + startTime + "-" + endTime + "] and weekly interal of [" + startDayOfWeek + "-" + endDayOfWeek + "].");

    }

    /**
     * ! Convert a time string in the format "HH:mm" to a cron expression that
     * triggers every day at that time.
     * ! Returns null if the input is not a valid time string.
     *
     * @param time the time in the format "HH:mm".
     * @return a cron expression string or null.
     */

    private String convertToCronExpression(String time) {
        try {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return String.format("0 %d %d * * *", localTime.getMinute(), localTime.getHour());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * ! Convert a time string in the format "HH:mm", start day of week, and end day
     * of week
     * ! to a cron expression that triggers every day at that time between start and
     * end day of week.
     * ! Returns null if the input is not valid.
     *
     * @param time           the time in the format "HH:mm".
     * @param startDayOfWeek the start day of week in string format.
     * @param endDayOfWeek   the end day of week in string format.
     * @return a cron expression string or null.
     */

    private String convertToCronExpression(String time, String startDayOfWeek, String endDayOfWeek) {
        try {
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

            // Fix common mistakes in startDayOfWeek
            String normalizedStartDay = fixDayOfWeek(startDayOfWeek);

            // Fix common mistakes in endDayOfWeek
            String normalizedEndDay = fixDayOfWeek(endDayOfWeek);

            int startDay = DayOfWeek.valueOf(normalizedStartDay).getValue();
            int endDay = DayOfWeek.valueOf(normalizedEndDay).getValue();

            // Validate start and end day range
            if (startDay > endDay) {
                throw new IllegalArgumentException("Invalid day range");
            }

            return String.format("0 %d %d ? * %d-%d", localTime.getMinute(), localTime.getHour(), startDay, endDay);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * ! Convert an interval string in the format "HH:mm", start time string in the
     * format "HH:mm",
     * ! end time string in the format "HH:mm", start day of week, and end day of
     * week
     * ! to a cron expression that triggers every interval between start and end
     * time of day,
     * ! between start and end day of week.
     * ! Returns null if the input is not valid.
     *
     * @param interval       the interval in the format "HH:mm".
     * @param startTime      the start time in the format "HH:mm".
     * @param endTime        the end time in the format "HH:mm".
     * @param startDayOfWeek the start day of week in string format.
     * @param endDayOfWeek   the end day of week in string format.
     * @return CronExpression string or null.
     */

    private String convertToCronExpression(String interval, String startTime, String endTime, String startDayOfWeek,
            String endDayOfWeek) {
        try {
            // Validate interval format
            LocalTime intervalTime = LocalTime.parse(interval, DateTimeFormatter.ofPattern("HH:mm"));

            // Validate start and end time format
            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));

            if (end.isBefore(start)) {
                throw new IllegalArgumentException("Invalid time range");
            }

            // Fix common mistakes in startDayOfWeek
            String normalizedStartDay = fixDayOfWeek(startDayOfWeek);

            // Fix common mistakes in endDayOfWeek
            String normalizedEndDay = fixDayOfWeek(endDayOfWeek);

            int startDay = DayOfWeek.valueOf(normalizedStartDay).getValue();
            int endDay = DayOfWeek.valueOf(normalizedEndDay).getValue();

            // Validate start and end day range
            if (startDay > endDay) {
                throw new IllegalArgumentException("Invalid day range");
            }

            // Generate cron expression
            return String.format("0 */%d %d-%d ? * %d-%d", intervalTime.getMinute(), start.getHour(), end.getHour(),
                    startDay, endDay);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return null; // Return null for invalid input
        }
    }

    private String fixDayOfWeek(String dayOfWeek) {
        // Remove leading/trailing whitespace
        String trimmedDayOfWeek = dayOfWeek.trim();

        // Convert the first letter to uppercase
        return Character.toUpperCase(trimmedDayOfWeek.charAt(0)) + trimmedDayOfWeek.substring(1);
    }

    public void assignPackagesAutomatically() {
        // assignPackages();

        System.out.println(
                "\u001B[91;1mALIVE BITCHES IM ALIVE AHAHAHAHAHAHAHAHAHAHAHAAHAHAH BITCH BITCH... PLEASE\u001B[0m");
    }

    @PostMapping("/assign-to-driver")
    public ResponseEntity<Map<String, Object>> assignPackages() {
        try {

            LocalDate today = LocalDate.now(ZoneOffset.UTC); // Get the current date in Universal Time

            LocalDateTime sd = LocalDateTime.of(today, LocalTime.of(0, 0, 0));
            LocalDateTime ed = LocalDateTime.of(today.plusDays(1), LocalTime.of(0, 0, 0));

            Date startDate = Date.from(sd.atZone(ZoneOffset.UTC).toInstant());
            Date endDate = Date.from(ed.atZone(ZoneOffset.UTC).toInstant());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = today.format(formatter);

            List<Package> todayPackages = packageRepository
                    .findByAssignedToDriverIsNullAndDeliveryDateBetween(startDate, endDate);

            // Sort postponed packages in descending order of their postponed count.
            Comparator<Package> postponedComparator = Comparator.comparing(Package::getPostponedCount).reversed();
            todayPackages.sort(postponedComparator);

            for (Package packageObj : todayPackages) {

                // For each package find suitable driversr
                List<Driver> drivers = driverRepository.findAll();
                List<Driver> matchingDrivers = getMatchingDrivers(drivers, packageObj);
                Driver availableDriver = getAvailableDriver(matchingDrivers);

                if (availableDriver != null) {
                    assignPackageToDriver(availableDriver, packageObj);

                    Assignment assignmentG = DPAService.createAssignment(packageObj.getId().toString(),
                            availableDriver.getId().toString(), "null", "Assigned", packageObj.getPostponedCount(),
                            false);

                    DPAService.addAssignment(packageObj.getId().toString(), date, false, assignmentG);

                } else {
                    postponePackage(packageObj);

                    Assignment assignmentP = DPAService.createAssignment(packageObj.getId().toString(),
                            null, null, "Postpond", packageObj.getPostponedCount(),
                            false);

                    DPAService.addAssignment(packageObj.getId().toString(), date, false, assignmentP);

                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Package Assignment Successful.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Package Assignment Failed.");
            response.put("exception", e.getClass().getSimpleName());
            response.put("message", e.getMessage());
            response.put("stackTrace", getStackTraceAsString(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    private List<Driver> getMatchingDrivers(List<Driver> drivers, Package packageObj) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(formatter);

        List<Driver> onDuty = drivers.stream()
                .filter(driver -> {
                    boolean isOnDuty = DriverWFMService.isDriverOnDuty(driver.getId().toString());
                    System.out.println("Driver " + driver.getId() + " is " + (isOnDuty ? "on duty" : "off duty"));
                    return isOnDuty;
                })
                .collect(Collectors.toList());

        DeliveryLocation packageDeliveryLocation = packageObj.getDeliveryLocation();

        List<Driver> matchingDrivers = onDuty.stream()
                .filter(driver -> {
                    boolean hasMatchingLocation = driver.getDeliveryLocations().stream()
                            .anyMatch(location -> location.equals(packageDeliveryLocation));
                    System.out.println("Driver " + driver.getId() + " has "
                            + (hasMatchingLocation ? "matching" : "non-matching") + " location");
                    return hasMatchingLocation;
                })
                .filter(driver -> {
                    int assignedPackageCount = driverPackageAssignmentService
                            .getDriverAssignedPackageCount(driver.getId(), date);
                    boolean isBelowMaxCapacity = assignedPackageCount < driver.getMaxCapacity();
                    System.out.println(
                            "Driver " + driver.getId() + " has " + assignedPackageCount + " assigned packages");
                    System.out.println("Driver " + driver.getId() + " is "
                            + (isBelowMaxCapacity ? "below" : "at or above") + " maximum capacity");
                    return isBelowMaxCapacity;
                })
                .collect(Collectors.toList());

        return matchingDrivers;
    }

    // ? package pacakgeobj = (find the pacakge bla bla bla....);
    // ! packageObj.getDeliveryLocation().get("longitude");
    // ! packageObj.getDeliveryLocation().get("latitude");
    // ! packageObj.getDeliveryLocation().get("city");
    // ! packageObj.getDeliveryLocation().get("Zip");
    // ! packageObj.getDeliveryLocation().get("street");

    private Driver getAvailableDriver(List<Driver> drivers) {

        LocalDate today = LocalDate.now(ZoneOffset.UTC); // Get the current date in Universal Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(formatter);

        if (drivers.isEmpty()) {
            return null;
        }

        // Sort drivers by the number of packages they are currently handling
        Comparator<Driver> packageCountComparator = Comparator.comparing(
                driver -> driverPackageAssignmentService.getDriverAssignedPackageCount(driver.getId(), date));
        drivers.sort(packageCountComparator);

        return drivers.get(0);
    }

    private void assignPackageToDriver(Driver driver, Package packageObj) {

        LocalDate today = LocalDate.now(ZoneOffset.UTC); // Get the current date in Universal Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(formatter);

        // Create a new list and add the packageId to it
        List<ObjectId> packageIds = new ArrayList<>();
        packageIds.add(packageObj.getId());

        driverPackageAssignmentService.assignPackagestodriver(date, driver.getId(), packageIds);
        packageObj.setStatus("Assigned");
        packageObj.setAssignedToDriver(driver.getId());
        packageRepository.save(packageObj);
    }

    private void postponePackage(Package packageObj) {
        packageObj.setStatus("Postponed");
        packageObj.setPostponedCount(packageObj.getPostponedCount() + 1);

        Date deliveryDate = packageObj.getDeliveryDate();
        LocalDate localDate = deliveryDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
        LocalDate nextDate = localDate.plusDays(1);
        Date convertedDate = Date.from(nextDate.atStartOfDay(ZoneId.of("UTC")).toInstant());

        packageObj.setDeliveryDate(convertedDate);

        packageRepository.save(packageObj);
    }

    /*
     * @GetMapping("/{id}/location")
     * public ResponseEntity<double[]> getPackageLocation(@PathVariable("id")
     * ObjectId id) {
     * double[] location = packageService.getLongitudeAndLatitude(id);
     * if (location != null) {
     * return ResponseEntity.ok(location);
     * }
     * return ResponseEntity.notFound().build();
     * }
     */

    @PutMapping("/assign-to-driver-by-id")
    public ResponseEntity<Map<String, Object>> assignPackagesForcefully(@RequestParam String pkgId,
            @RequestParam String drvId) {

        LocalDate today = LocalDate.now(ZoneOffset.UTC); // Get the current date in Universal Time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(formatter);

        ObjectId driverId = new ObjectId(drvId);
        ObjectId packageId = new ObjectId(pkgId);

        Map<String, Object> response = new HashMap<>();

        Package packageObj = packageRepository.findById(packageId).orElse(null);
        Driver driver = driverRepository.findById(driverId).orElse(null);

        if (packageObj == null) {
            response.put("message", "Package not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (driver == null) {
            response.put("message", "Driver not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (driverPackageAssignmentService.getDriverAssignedPackageCount(driver.getId(), date) >= driver
                .getMaxCapacity()) {
            response.put("message", "Driver has reached maximum capacity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (packageObj.getAssignedToDriver() != null) {
            response.put("message", "Package is already assigned to a driver.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Assign package to Driver
        assignPackageToDriver(driver, packageObj);
        packageObj.setForced(true);
        packageRepository.save(packageObj);

        Assignment assignment = DPAService.createAssignment(packageObj.getId().toString(), driver.getId().toString(),
                "null", "Assigned", packageObj.getPostponedCount(), true);
        DPAService.addAssignment(packageObj.getId().toString(), date, true, assignment);

        response.put("message", "Forced Package assignment to driver successfully.");
        response.put("data", packageObj);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{driverObjectId}/status")
    public ResponseEntity<Boolean> isDriverOnDuty(@PathVariable String driverObjectId) {

        return ResponseEntity.ok(DriverWFMService.isDriverOnDuty(driverObjectId));
    }

    @GetMapping("/{driverId}/schedule")
    public ResponseEntity<List<WorkDay>> getDriverSchedule(@PathVariable String driverId, @RequestParam int week) {
        try {
            List<WorkDay> schedule = DriverWFMService.getDriverWorkDays(driverId, week);
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/schedules/{week}")
    public ResponseEntity<List<DriverSchedule>> getAllDriverSchedulesForWeek(@PathVariable int week) {
        try {
            List<DriverSchedule> driverSchedules = DriverWFMService.getAllDriverSchedulesForWeek(week);
            return ResponseEntity.ok(driverSchedules);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    // ALL DRIVER SCHEDUES FOR A GIVEN WEEK AND DAY,
    @GetMapping("/schedules/{week}/{day}")
    public ResponseEntity<List<DriverWorkDays>> getAllDriverSchedulesForWeekAndDay(
            @PathVariable int week, @PathVariable String day) {
        List<DriverWorkDays> driverSchedules = DriverWFMService.getDriverSchedulesForWeekAndDay(week, day);
        return new ResponseEntity<>(driverSchedules, HttpStatus.OK);
    }

    // SPECIFIC DRIVER SCHEDUES FOR A GIVEN WEEK AND DAY,
    @GetMapping("/schedules/{week}/{day}/{driver}")
    public ResponseEntity<List<DriverWorkDays>> getDriverScheduleForWeekAndDay(
            @PathVariable int week, @PathVariable String day, @PathVariable String driver) {
        List<DriverWorkDays> driverSchedules = DriverWFMService.getDriverScheduleForWeekDayAndDriver(week, day, driver);
        return new ResponseEntity<>(driverSchedules, HttpStatus.OK);
    }

    // SPECIFIC DRIVER SCHEDUES FOR A GIVEN WEEK AND DAY,
    @GetMapping("/schedules/Active-CurrentWeek-Drivers")
    public List<String> getActiveDriverIds() {
        // Get the active driver IDs for the current week
        return DriverWFMService.getActiveDriverIds();
    }

    @GetMapping("/schedules/Active-CurrentDay-Drivers")
    public List<String> getActiveDriversForCurrentDay() {
        return DriverWFMService.getActiveDriverIdsForDay();
    }

    @GetMapping("/schedules/Active-CurrentTime-Drivers")
    public ResponseEntity<List<String>> getActiveDriversForCurrentTime() {
        List<String> activeDriverIds = DriverWFMService.getActiveDriverIdsForCurrentTime();
        return new ResponseEntity<>(activeDriverIds, HttpStatus.OK);
    }

    @PostMapping("/scheduleUpdates")
    public ResponseEntity<List<DriverWorkDays>> updateDriverSchedule(@RequestBody JsonNode requestBody) {
        String driverId = requestBody.get("driverId").asText();
        int week = requestBody.get("week").asInt();
        String input = requestBody.get("input").asText();

        DriverWFMService.updateDriverSchedule(driverId, week, input);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/DailyScheduleUpdates")
    public ResponseEntity<String> updateDriverDailySchedule(@RequestBody JsonNode request) {
        DriverWFMService.updateDriverDailySchedule(
                request.get("driverId").asText(),
                request.get("week").asInt(),
                request.get("dayOfWeek").asText(),
                request.get("workingHours"));
        return ResponseEntity.ok("Driver schedule updated successfully.");
    }

    @DeleteMapping("/DeleteDriverWorkDay")
    public ResponseEntity<String> deleteDriverWorkDay(@RequestParam String driverId, @RequestParam int week,
            @RequestParam String dayOfWeek) {
        try {
            DriverWFMService.deleteDriverWorkDay(driverId, week, dayOfWeek);
            return ResponseEntity.ok("Driver work day deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "You're trying to preform an impossible action (eg: Deleting a schedule that dosn't exist.) or An unexpected error occurred");
        }
    }

    @DeleteMapping("/DeleteDriverWeekSchedule")
    public ResponseEntity<String> deleteDriverSchedule(
            @RequestParam("driverId") String driverId,
            @RequestParam("week") int week) {
        DriverWFMService.deleteDriverSchedule(driverId, week);
        return ResponseEntity
                .ok("DriverSchedule for driver ID " + driverId + " in week " + week + " has been deleted.");
    }

    @DeleteMapping("/DeleteDriveSchedule")
    public ResponseEntity<String> deleteDriverScheduleByWeek(@RequestParam("week") int week) {
        DriverWFMService.deleteDriverScheduleByWeek(week);
        return ResponseEntity.ok("DriverSchedule for week " + week + " has been deleted.");
    }

    @GetMapping("/drivers/{driverId}/weekly-hours")
    public double calculateWeeklyHours(@PathVariable String driverId, @RequestParam int week) {
        return DriverWFMService.calculateWeeklyHours(driverId, week);
    }

    @GetMapping("/{driverId}/daily-hours")
    public ResponseEntity<Double> calculateDailyHours(
            @PathVariable String driverId,
            @RequestParam int week,
            @RequestParam String dayOfWeek) {
        double hours = DriverWFMService.calculateDailyHours(driverId, week, dayOfWeek);
        return ResponseEntity.ok(hours);
    }

}
