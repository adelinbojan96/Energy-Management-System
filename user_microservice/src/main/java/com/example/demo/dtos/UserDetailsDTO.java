package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class UserDetailsDTO {

    private UUID id;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    private String email;

    @NotNull(message = "age is required")
    private Integer age;

    @NotBlank(message = "role is required")
    private String role;

    private UUID credentialId;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(String name, String email, int age, String role,UUID credentialId) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = role;
        this.credentialId = credentialId;
    }

    public UserDetailsDTO(UUID id, String name, String email, int age, String role, UUID credentialId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.role = role;
        this.credentialId = credentialId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setAddress(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UUID getCredentialId() {
        return credentialId;
    }
    public void setCredentialId(UUID credentialId) {
        this.credentialId = credentialId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsDTO that = (UserDetailsDTO) o;
        return Objects.equals(age, that.age) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, age);
    }
}
