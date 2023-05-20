package com.example.packageassignment.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.example.packageassignment.model.Driver;
import com.example.packageassignment.repository.DriverRepository;

@Service
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public ObjectId findDriverIdByName(String name) {
        List<Driver> drivers = driverRepository.findByNameContainingIgnoreCase(name);
        if (drivers.isEmpty()) {
            // Handle the case where no matching driver is found
            throw new IllegalArgumentException("No matching driver found for the given name: " + name);
        }
        // Assuming there is only one driver with the given name, return its ID
        return drivers.get(0).getId();
    }
}
