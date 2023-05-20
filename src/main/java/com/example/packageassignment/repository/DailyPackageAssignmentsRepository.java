package com.example.packageassignment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.packageassignment.model.DailyPackageAssignments;

public interface DailyPackageAssignmentsRepository extends MongoRepository<DailyPackageAssignments, String> {
    DailyPackageAssignments findByDate(String date);
}
