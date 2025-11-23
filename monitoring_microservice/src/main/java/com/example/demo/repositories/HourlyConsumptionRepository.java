package com.example.demo.repositories;

import com.example.demo.entities.HourlyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HourlyConsumptionRepository extends JpaRepository<HourlyConsumption, UUID> {

    Optional<HourlyConsumption> findByDeviceIdAndTimestamp(UUID deviceId, LocalDateTime timestamp);

    List<HourlyConsumption> findByDeviceIdInAndTimestampBetween(List<UUID> deviceIds, LocalDateTime start, LocalDateTime end);
}