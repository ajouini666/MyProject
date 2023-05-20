package com.example.packageassignment.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "packages")
public class Package {
    @Id
    private ObjectId id;
    private String description;
    private DeliveryLocation deliveryLocation;
    private Date deliveryDate;
    private String status;
    private String action;
    private int postponedCount;
    private ObjectId assignedToDriver;
    private Boolean forced;

    public Package() {
    }

    public Package(String description, DeliveryLocation deliveryLocation, Date deliveryDate,
            String status, int postponedCount, ObjectId assignedToDriver) {

        this.description = description;
        this.deliveryLocation = deliveryLocation;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.postponedCount = postponedCount;
        this.assignedToDriver = assignedToDriver;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Boolean getForced() {
        return forced;
    }

    public void setForced(Boolean forced) {
        this.forced = forced;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DeliveryLocation getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(DeliveryLocation deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
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

    public ObjectId getAssignedToDriver() {
        return assignedToDriver;
    }

    public void setAssignedToDriver(ObjectId assignedToDriver) {
        this.assignedToDriver = assignedToDriver;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean hasMatchingDriver(Driver driver) {
        DeliveryLocation packageDeliveryLocation = getDeliveryLocation();
        return driver.hasMatchingDeliveryLocation(packageDeliveryLocation);
    }

}