package com.example.packageassignment.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.example.packageassignment.model.DriverPackageAssignment;

import java.util.List;

@Service
public class DriverPackageAssignmentService {

    private final MongoTemplate mongoTemplate;

    public DriverPackageAssignmentService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void assignPackagestodriver(String date, ObjectId driverId, List<ObjectId> packageIds) {

        Query query = new Query(
                Criteria.where("driverId").is(driverId)
                        .and("date").is(date));

        DriverPackageAssignment assignment = mongoTemplate.findOne(query, DriverPackageAssignment.class);

        if (assignment != null) {
            // Case 1: Driver already has assignments on that day, add new assignments to
            // existing ones
            List<ObjectId> existingPackageIds = assignment.getPackageIds();
            existingPackageIds.addAll(packageIds);

            Update update = new Update().set("packageIds", existingPackageIds);
            mongoTemplate.updateFirst(query, update, DriverPackageAssignment.class);
        } else {
            // Case 2: Driver hasn't been assigned any packages on that day, create a new
            // document
            DriverPackageAssignment newAssignment = new DriverPackageAssignment(date, driverId, packageIds);
            mongoTemplate.save(newAssignment);
        }
    }

    public int getDriverAssignedPackageCount(ObjectId driverId, String date) {

        Query query = new Query(
                Criteria.where("driverId").is(driverId)
                        .and("date").is(date));

        DriverPackageAssignment assignment = mongoTemplate.findOne(query, DriverPackageAssignment.class);

        if (assignment != null) {
            return assignment.getPackageIds().size();
        } else {
            return 0;
        }
    }
}
