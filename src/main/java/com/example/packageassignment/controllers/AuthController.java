package com.example.packageassignment.controllers;

import com.example.packageassignment.Security.jwt.JwtUtils;
import com.example.packageassignment.Security.services.EmployeeDetailsImpl;
import com.example.packageassignment.model.Driver;
import com.example.packageassignment.models.ERole;
import com.example.packageassignment.models.Role;
import com.example.packageassignment.repository.DriverRepository;
import com.example.packageassignment.repository.RoleRepository;
import com.example.packageassignment.requests.LoginRequest;
import com.example.packageassignment.requests.SignupRequest;
import com.example.packageassignment.responses.JwtResponse;
import com.example.packageassignment.responses.MessageResponse;

import com.example.packageassignment.service.SecurityEncryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        DriverRepository driverRepository;

        @Autowired
        SecurityEncryption SecurityEncryption;

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder encoder;

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping("/signin")
        public ResponseEntity<?> authenticateEmployee(@RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(
                                                loginRequest.getEmployeename(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                EmployeeDetailsImpl employeeDetails = (EmployeeDetailsImpl) authentication.getPrincipal();
                List<String> roles = employeeDetails.getAuthorities()
                                .stream().map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt,
                                employeeDetails.getId().toString(),
                                employeeDetails.getUsername(),
                                employeeDetails.getEmail(), roles));
        }

        @PostMapping("/signup")

        public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {

                String strRole = SecurityEncryption.decryptCode(signUpRequest.getCode());

                Logger logger = LoggerFactory.getLogger(AuthController.class);
                logger.debug("request is {}", strRole, signUpRequest.getEmployeename());

                if (Boolean.TRUE.equals(driverRepository.existsByEmployeename(signUpRequest.getEmployeename()))) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Error: Employeename is already taken!"));
                }

                if (Boolean.TRUE.equals(driverRepository.existsByEmail(signUpRequest.getEmail()))) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
                }

                // getRole returns either "ROLE_EMPLOYEE", "ROLE_ADMIN"

                if (strRole == null) {

                        return ResponseEntity.ok(new MessageResponse(
                                        "Invalid Registration code, please contact your administrator or review the code entered."));

                } else {

                        // Create new employee account
                        Driver employee = new Driver(signUpRequest.getEmployeename(), signUpRequest.getEmail(),
                                        encoder.encode(signUpRequest.getPassword()));

                        if (strRole.equals("ROLE_ADMIN")) {
                                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Role is not found [ROLE_ADMIN]"));
                                employee.setRole(adminRole);
                        } else if (strRole.equals("ROLE_EMPLOYEE")) {
                                Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Role is not found [ROLE_EMPLOYEE]"));
                                employee.setRole(employeeRole);
                        }

                        driverRepository.save(employee);
                        return ResponseEntity.ok(new MessageResponse("Employee registered successfully!"));

                }

        }

}
