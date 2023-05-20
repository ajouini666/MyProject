package com.example.packageassignment.model;

public class WorkDay {
    private String dayOfWeek;
    private WorkingHours workingHours;

    public WorkDay() {
    }

    public WorkDay(String dayOfWeek, WorkingHours workingHours) {
        this.dayOfWeek = dayOfWeek;
        this.workingHours = workingHours;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }
}
