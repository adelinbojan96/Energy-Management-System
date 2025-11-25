package com.example.demo.repositories;

import com.example.demo.entities.DeviceUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeviceUserMappingRepository extends JpaRepository<DeviceUserMapping, UUID> {

    List<DeviceUserMapping> findByUserId(UUID userId);

    void deleteByDeviceId(UUID deviceId);
}