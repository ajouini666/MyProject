package com.example.packageassignment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.packageassignment.model.DriverSchedule;

public interface DriverScheduleRepository extends MongoRepository<DriverSchedule, String> {

    List<DriverSchedule> findByWeek(int week);

    DriverSchedule findAllByWeek(int week);

    DriverSchedule findByDriverSchedulesDriverId(String driverId);

    DriverSchedule findByWeekAndDriverSchedulesDriverId(int week, String driverId);

}
