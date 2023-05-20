package com.example.packageassignment.Security.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.packageassignment.model.Driver;
import com.example.packageassignment.models.ERole;
import com.example.packageassignment.models.Role;
import com.example.packageassignment.repository.DriverRepository;
import com.example.packageassignment.repository.RoleRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeDetailsServiceImpl implements UserDetailsService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String Employeename)
            throws UsernameNotFoundException {

        Driver employee = driverRepository
                .findByEmployeename(Employeename)
                .orElseThrow(() -> new UsernameNotFoundException("Driver Not Found with username: "
                        + Employeename));

        return EmployeeDetailsImpl.build(employee);
    }

    public Driver updateEmployee(ObjectId employeeId, Driver employee) {
        Driver existingEMP = driverRepository.findById(employeeId).orElse(null);

        if (existingEMP == null) {
            return null; // Or handle the case where employee is not found
        }

        existingEMP.setEmployeename(employee.getEmployeename());
        existingEMP.setEmail(employee.getEmail());

        Role role = employee.getRole(); // Get the Role object

        if (role != null) {
            String roleName = role.getName().toString(); // Extract the role name as a String

            switch (roleName) {
                case "ROLE_ADMIN":
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN.toString());
                    if (adminRole != null) {
                        // Role found
                        existingEMP.setRole(adminRole);
                    } else {
                        throw new RuntimeException("Error: Role is not found.");
                    }
                    break;
                case "ROLE_EMPLOYEE":
                    Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE.toString());
                    if (employeeRole != null) {
                        // Role found
                        existingEMP.setRole(employeeRole);
                    } else {
                        throw new RuntimeException("Error: Role is not found.");
                    }
                    break;
                default:
                    throw new RuntimeException("Error: Invalid role name");
            }
        }

        return driverRepository.save(existingEMP);
    }

}