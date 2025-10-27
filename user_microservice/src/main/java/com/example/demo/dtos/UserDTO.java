package com.example.demo.dtos;

import java.util.Objects;
import java.util.UUID;

public class UserDTO {
    private UUID id;
    private String name;
    private int age;
    private String email;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(UUID credentialId) {
        this.credentialId = credentialId;
    }

    private String role;
    private UUID credentialId;

    public UserDTO() {}
    public UserDTO(UUID id, String name, int age) {
        this.id = id; this.name = name; this.age = age;
    }

    public UserDTO(UUID id, String name, String email, int age, String role, UUID credentialId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.role = role;
        this.credentialId = credentialId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO that = (UserDTO) o;
        return age == that.age && Objects.equals(name, that.name);
    }
    @Override public int hashCode() { return Objects.hash(name, age); }
}
