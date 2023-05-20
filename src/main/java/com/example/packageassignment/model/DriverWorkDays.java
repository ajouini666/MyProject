package com.example.packageassignment.model;

import java.util.List;

public class DriverWorkDays {
    private String driverId;
    private List<WorkDay> workDays;

    public DriverWorkDays() {
    }

    public DriverWorkDays(String driverId, List<WorkDay> workDays) {
        this.driverId = driverId;
        this.workDays = workDays;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public List<WorkDay> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(List<WorkDay> workDays) {
        this.workDays = workDays;
    }
}