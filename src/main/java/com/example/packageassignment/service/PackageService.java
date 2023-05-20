package com.example.packageassignment.service;

import com.example.packageassignment.model.Package;
import com.example.packageassignment.repository.PackageRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Package getPackageById(ObjectId id) {
        return packageRepository.findById(id).orElse(null);
    }

    public Package savePackage(Package packageObj) {
        return packageRepository.save(packageObj);
    }

    public void deletePackage(ObjectId id) {
        packageRepository.deleteById(id);
    }

    /*
     * public double[] getLongitudeAndLatitude(ObjectId id) {
     * Package packageObj = packageRepository.findById(id).orElse(null);
     * if (packageObj != null) {
     * Double longitude =
     * Double.parseDouble(packageObj.getDeliveryLocation().get("longitude"));
     * Double latitude =
     * Double.parseDouble(packageObj.getDeliveryLocation().get("latitude"));
     * 
     * return new double[] { longitude, latitude };
     * 
     * // ? package pacakgeobj = (find the pacakge bla bla bla....);
     * // ! packageObj.getDeliveryLocation().get("longitude");
     * // ! ;
     * // ! packageObj.getDeliveryLocation().get("city");
     * // ! packageObj.getDeliveryLocation().get("Zip");
     * // ! packageObj.getDeliveryLocation().get("street");
     * }
     * return new double[0];
     * }
     */
}