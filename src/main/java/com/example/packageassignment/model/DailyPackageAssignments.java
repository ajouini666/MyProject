package com.example.packageassignment.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DailyPackageAssignments")
public class DailyPackageAssignments {
    @Id
    private String id;
    private String date;
    private List<Assignment> assignments;

    public DailyPackageAssignments() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DailyPackageAssignments(String date) {
        this.date = date;
        this.assignments = new ArrayList<>();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}