package com.example.demo.repositories;

import com.example.demo.entities.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    @Query("SELECT u FROM Credential u WHERE u.username = :username")
    Optional<Credential> findByUsername(String username);

}
