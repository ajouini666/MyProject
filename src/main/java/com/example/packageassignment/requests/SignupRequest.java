package com.example.packageassignment.requests;

import java.util.Objects;

public class SignupRequest {
    private String employeename;
    private String email;
    private String code; // Change to a single role instead of Set<String>
    private String password;

    public SignupRequest() {
    }

    public SignupRequest(String employeename, String email, String code, String password) {
        this.employeename = employeename;
        this.email = email;
        this.code = code;
        this.password = password;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SignupRequest employeename(String employeename) {
        setEmployeename(employeename);
        return this;
    }

    public SignupRequest email(String email) {
        setEmail(email);
        return this;
    }

    public SignupRequest code(String code) {
        setCode(code);
        return this;
    }

    public SignupRequest password(String password) {
        setPassword(password);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SignupRequest)) {
            return false;
        }
        SignupRequest signupRequest = (SignupRequest) o;
        return Objects.equals(employeename, signupRequest.employeename) && Objects.equals(email, signupRequest.email)
                && Objects.equals(code, signupRequest.code) && Objects.equals(password, signupRequest.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeename, email, code, password);
    }

    @Override
    public String toString() {
        return "{" +
                " employeename='" + getEmployeename() + "'" +
                ", email='" + getEmail() + "'" +
                ", code='" + getCode() + "'" +
                ", password='" + getPassword() + "'" +
                "}";
    }

    public String getEmployeename() {
        return employeename;
    }

    public void setEmployeename(String employeename) {
        this.employeename = employeename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/*
 * public class SignupRequest {
 * private String employeename;
 * 
 * private String email;
 * 
 * private Set<String> roles;
 * 
 * private String password;
 * 
 * public String getEmployeename() {
 * return employeename;
 * }
 * 
 * public void setEmployeename(String employeename) {
 * this.employeename = employeename;
 * }
 * 
 * public String getEmail() {
 * return email;
 * }
 * 
 * public void setEmail(String email) {
 * this.email = email;
 * }
 * 
 * public String getPassword() {
 * return password;
 * }
 * 
 * public void setPassword(String password) {
 * this.password = password;
 * }
 * 
 * public Set<String> getRoles() {
 * return this.roles;
 * }
 * 
 * public void setRoles(Set<String> roles) {
 * this.roles = roles;
 * }
 * }
 */
