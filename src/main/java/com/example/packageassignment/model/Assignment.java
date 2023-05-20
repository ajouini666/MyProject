package com.example.packageassignment.model;

public class Assignment {
    private String packageId;
    private String driverId;
    private String action;
    private String status;
    private int postponedCount;
    private boolean forced;

    public Assignment() {
        // Default constructor
    }

    public Assignment(String packageId, String driverId, String action, String status, int postponedCount) {
        this.packageId = packageId;
        this.driverId = driverId;
        this.action = action;
        this.status = status;
        this.postponedCount = postponedCount;
        this.forced = false;
    }

    public Assignment(String packageId, String driverId, String action, String status, int postponedCount,
            boolean forced) {
        this.packageId = packageId;
        this.driverId = driverId;
        this.action = action;
        this.status = status;
        this.postponedCount = postponedCount;
        this.forced = forced;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPostponedCount() {
        return postponedCount;
    }

    public void setPostponedCount(int postponedCount) {
        this.postponedCount = postponedCount;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}