package com.example.packageassignment.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.packageassignment.responses.MessageResponse;

//@CrossOrigin(origins = "*", maxAge = 4800)
@RestController
@RequestMapping("/api/test")
public class EmployeeController {

	@GetMapping("/all")
	public MessageResponse allAccess() {
		return new MessageResponse("Public ");
	}

	@GetMapping("/employee")
	@PreAuthorize("hasRole('EMPLOYEE') ")
	public MessageResponse employeeAccess() {

		return new MessageResponse("Employee zone");
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public MessageResponse adminAccess() {
		return new MessageResponse("Admin zone");
	}
}
