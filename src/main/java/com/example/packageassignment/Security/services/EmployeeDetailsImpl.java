package com.example.packageassignment.Security.services;

import com.example.packageassignment.model.Driver;
import com.example.packageassignment.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EmployeeDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private ObjectId id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public EmployeeDetailsImpl(ObjectId id,
            String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static EmployeeDetailsImpl build(Driver user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null) {
            Role role = user.getRole(); // Get the single role directly
            authorities.add(new SimpleGrantedAuthority(role.getName().name()));
        }

        return new EmployeeDetailsImpl(user.getId(), user.getEmployeename(),
                user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities;
    }

    public ObjectId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmployeeDetailsImpl user = (EmployeeDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
