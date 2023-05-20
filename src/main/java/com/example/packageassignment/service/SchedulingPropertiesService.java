package com.example.packageassignment.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.packageassignment.model.SchedulingProperties;

@Service
public class SchedulingPropertiesService {

    private final MongoTemplate mongoTemplate;

    public SchedulingPropertiesService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void updateScheduleEnabled(boolean enabled) {
        updateSchedulingProperty("scheduleEnabled", enabled);
    }

    public void updateDailySchedule(String cronExpression) {
        updateSchedulingProperty("cronExpression", cronExpression);
    }

    public boolean getScheduleEnabled() {
        SchedulingProperties schedulingProperties = getSchedulingProperties();
        return schedulingProperties.getScheduleEnabled();
    }

    public String getCronExpression() {
        SchedulingProperties schedulingProperties = getSchedulingProperties();
        return schedulingProperties.getCronExpression();
    }

    private SchedulingProperties getSchedulingProperties() {
        Query query = new Query(Criteria.where("_id").is(new ObjectId("6457cdc6b9927b6a350965ba")));
        SchedulingProperties schedulingProperties = mongoTemplate.findOne(query, SchedulingProperties.class);
        if (schedulingProperties == null) {
            schedulingProperties = createDefaultSchedulingProperties();
        }
        return schedulingProperties;
    }

    private SchedulingProperties createDefaultSchedulingProperties() {
        SchedulingProperties schedulingProperties = new SchedulingProperties();
        schedulingProperties.setId(new ObjectId("6457cdc6b9927b6a350965ba"));
        schedulingProperties.setCronExpression("0 7 * * *"); // Set cronExpression to "0 7 * * *" (default)
        schedulingProperties.setScheduleEnabled(false); // Set scheduleEnabled to false (default)
        schedulingProperties.setInterval(3600); // Set interval to 3600 seconds (1h) (default)
        return schedulingProperties;
    }

    private void updateSchedulingProperty(String fieldName, Object value) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId("6457cdc6b9927b6a350965ba")));
        Update update = new Update().set(fieldName, value);
        mongoTemplate.updateFirst(query, update, SchedulingProperties.class);
    }

    public void updateInterval(int interval) {
        updateSchedulingProperty("interval", interval);
    }

    public int getInterval() {
        SchedulingProperties schedulingProperties = getSchedulingProperties();
        return schedulingProperties.getInterval();
    }

}