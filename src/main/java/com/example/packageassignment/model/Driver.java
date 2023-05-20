package com.example.packageassignment.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.packageassignment.models.Role;

import java.util.List;
import java.util.Objects;

@Document(collection = "drivers")
public class Driver {
    @Id
    private ObjectId id;
    private String name;
    private int maxCapacity;
    private List<DeliveryLocation> deliveryLocations;

    private String email;

    private String password;

    private String employeename;

    @DBRef
    private Role role;

    public Driver(String employeename, String email, String password) {
        super();
        this.employeename = employeename;
        this.email = email;
        this.password = password;
    }

    public Driver(ObjectId id, String name, int maxCapacity, List<DeliveryLocation> deliveryLocations, String email,
            String password, Role role) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.deliveryLocations = deliveryLocations;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmployeename() {
        return this.employeename;
    }

    public void setEmployeename(String employeename) {
        this.employeename = employeename;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Driver id(ObjectId id) {
        setId(id);
        return this;
    }

    public Driver name(String name) {
        setName(name);
        return this;
    }

    public Driver maxCapacity(int maxCapacity) {
        setMaxCapacity(maxCapacity);
        return this;
    }

    public Driver deliveryLocations(List<DeliveryLocation> deliveryLocations) {
        setDeliveryLocations(deliveryLocations);
        return this;
    }

    public Driver email(String email) {
        setEmail(email);
        return this;
    }

    public Driver password(String password) {
        setPassword(password);
        return this;
    }

    public Driver role(Role role) {
        setRole(role);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Driver)) {
            return false;
        }
        Driver driver = (Driver) o;
        return Objects.equals(id, driver.id) && Objects.equals(name, driver.name) && maxCapacity == driver.maxCapacity
                && Objects.equals(deliveryLocations, driver.deliveryLocations) && Objects.equals(email, driver.email)
                && Objects.equals(password, driver.password) && Objects.equals(role, driver.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxCapacity, deliveryLocations, email, password, role);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", name='" + getName() + "'" +
                ", maxCapacity='" + getMaxCapacity() + "'" +
                ", deliveryLocations='" + getDeliveryLocations() + "'" +
                ", email='" + getEmail() + "'" +
                ", password='" + getPassword() + "'" +
                ", role='" + getRole() + "'" +
                "}";
    }

    public Driver(String name, int maxCapacity, List<DeliveryLocation> deliveryLocations) {
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.deliveryLocations = deliveryLocations;
    }

    public Driver() {
        // Default constructor required by Spring Data MongoDB
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return deliveryLocations;
    }

    public void setDeliveryLocations(List<DeliveryLocation> deliveryLocations) {
        this.deliveryLocations = deliveryLocations;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean hasMatchingDeliveryLocation(DeliveryLocation packageDeliveryLocation) {
        for (DeliveryLocation driverDeliveryLocation : deliveryLocations) {
            if (driverDeliveryLocation.equals(packageDeliveryLocation)) {
                return true;
            }
        }
        return false;
    }

}