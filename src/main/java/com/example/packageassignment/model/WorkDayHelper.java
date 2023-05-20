package com.example.packageassignment.model;

import java.util.ArrayList;
import java.util.List;

public class WorkDayHelper {

    private WorkDayHelper() {
        // Private constructor to hide the implicit public one
    }

    public static List<WorkDay> createWorkDays(List<WorkDayInput> workDayInputs) {
        List<WorkDay> workDays = new ArrayList<>();

        for (WorkDayInput workDayInput : workDayInputs) {
            String dayOfWeek = workDayInput.getDayOfWeek();
            String start = workDayInput.getWorkingHours().getStart();
            String end = workDayInput.getWorkingHours().getEnd();

            WorkDay workDay = new WorkDay(dayOfWeek, new WorkingHours(start, end));
            workDays.add(workDay);
        }

        return workDays;
    }

    public static class WorkDayInput {
        private String dayOfWeek;
        private WorkingHoursInput workingHours;

        public WorkDayInput(String dayOfWeek, WorkingHoursInput workingHours) {
            this.dayOfWeek = dayOfWeek;
            this.workingHours = workingHours;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public WorkingHoursInput getWorkingHours() {
            return workingHours;
        }

        public void setWorkingHours(WorkingHoursInput workingHours) {
            this.workingHours = workingHours;
        }
    }

    public static class WorkingHoursInput {
        private String start;
        private String end;

        public WorkingHoursInput(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
}