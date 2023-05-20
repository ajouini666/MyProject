package com.example.packageassignment.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "driver_package_assignments")
public class DriverPackageAssignment {
    @Id
    private ObjectId id;
    private String date;
    private ObjectId driverId;
    private List<ObjectId> packageIds = new ArrayList<>();

    public DriverPackageAssignment(String date, ObjectId driverId, List<ObjectId> packageIds) {
        this.date = date;
        this.driverId = driverId;
        this.packageIds = packageIds;
    }

    public DriverPackageAssignment() {
        // Default constructor required by Spring Data MongoDB
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ObjectId getDriverId() {
        return driverId;
    }

    public void setDriverId(ObjectId driverId) {
        this.driverId = driverId;
    }

    public List<ObjectId> getPackageIds() {
        return packageIds;
    }

    public void setPackageIds(List<ObjectId> packageIds) {
        this.packageIds = packageIds;
    }
}