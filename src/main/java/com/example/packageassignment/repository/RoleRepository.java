package com.example.packageassignment.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.packageassignment.models.ERole;
import com.example.packageassignment.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

	Role findByName(String name);

	Optional<Role> findByName(ERole name);
}
