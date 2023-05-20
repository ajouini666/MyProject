package com.example.packageassignment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "WFM")
public class DriverSchedule {
    @Id
    private String id;
    private int week;
    private List<DriverWorkDays> driverSchedules;

    public DriverSchedule() {
    }

    public DriverSchedule(int week, List<DriverWorkDays> driverSchedules) {
        this.week = week;
        this.driverSchedules = driverSchedules;
    }

    public String getDriverId() {
        return id;
    }

    public void setDriverId(String id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public List<DriverWorkDays> getDriverSchedules() {
        return driverSchedules;
    }

    public void setDriverSchedules(List<DriverWorkDays> driverSchedules) {
        this.driverSchedules = driverSchedules;
    }
}
