package com.example.packageassignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.packageassignment.models.ERole;
import com.example.packageassignment.models.Role;
import com.example.packageassignment.repository.RoleRepository;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.example.packageassignment")
public class PackageassignmentApplication implements CommandLineRunner {

	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(PackageassignmentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			if (roleRepository.findAll().isEmpty()) {
				Role role = new Role();
				role.setName(ERole.ROLE_EMPLOYEE);
				roleRepository.save(role);
				Role role2 = new Role();
				role2.setName(ERole.ROLE_ADMIN);
				roleRepository.save(role2);
			}
		} catch (Exception e) {

		}
	}

}