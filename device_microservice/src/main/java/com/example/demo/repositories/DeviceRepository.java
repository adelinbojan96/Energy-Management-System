package com.example.demo.repositories;

import com.example.demo.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    @Query("SELECT u FROM Device u WHERE u.name = :name")
    Optional<Device> findByName(@Param("name") String name);

    Optional<Device> findById(Integer id);
}
