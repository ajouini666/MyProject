package com.example.packageassignment.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.packageassignment.model.Package;

public interface PackageRepository extends MongoRepository<Package, ObjectId> {
    List<Package> findByAssignedToDriverIsNull();

    List<Package> findByAssignedToDriverIsNullAndDeliveryDateBetween(Date start, Date end);

    List<Package> findByDeliveryDateBetween(Date start, Date end);

    List<Package> findByAssignedToDriver(ObjectId driverId);

    List<Package> findByDeliveryDate(Date deliveryDate);

    List<Package> findByDeliveryDateGreaterThanEqualAndDeliveryDateLessThan(Date start, Date end);

    Optional<Package> findById(ObjectId objectId);

}