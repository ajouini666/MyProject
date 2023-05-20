package com.example.packageassignment.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
	@Id
	private String id;
	@Indexed(unique = true)
	private ERole name;

	public Role() {

	}

	public Role(String name) {
		this.name = convertToERole(name);
	}

	private ERole convertToERole(String roleName) {
		for (ERole role : ERole.values()) {
			if (role.toString().equalsIgnoreCase(roleName)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Invalid role name: " + roleName);
	}

	public Role(ERole name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

}