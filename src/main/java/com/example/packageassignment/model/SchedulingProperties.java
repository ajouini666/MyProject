package com.example.packageassignment.model;

import org.bson.types.ObjectId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "schedulingproperties")
@Document(collection = "GlobalSystemSettings")
public class SchedulingProperties {

    @Field("scheduleEnabled")
    private boolean scheduleEnabled;
    @Field("cronExpression")
    private String cronExpression;
    @Field("interval")
    private int interval;
    @Id
    private ObjectId id;
    // Getters and Setters

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public boolean getScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}