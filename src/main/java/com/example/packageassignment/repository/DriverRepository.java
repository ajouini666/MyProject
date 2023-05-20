package com.example.packageassignment.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.packageassignment.model.Driver;
import java.util.List;
import java.util.Optional;

public interface DriverRepository extends MongoRepository<Driver, ObjectId> {
    List<Driver> findByNameContainingIgnoreCase(String name);

    Optional<Driver> findByEmployeename(String employeename);

    Boolean existsByEmployeename(String employeename);

    Boolean existsByEmail(String email);

}