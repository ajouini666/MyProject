package com.example.packageassignment.repository;

import com.example.packageassignment.model.Assignment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssignmentRepository extends MongoRepository<Assignment, String> {
}
