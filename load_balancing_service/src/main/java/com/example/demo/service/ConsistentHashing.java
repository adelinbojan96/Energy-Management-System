package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;

@Service
public class ConsistentHashing {
    private final SortedMap<Integer, String> circle = new TreeMap<>();

    public ConsistentHashing() {
        addReplica("monitoring_queue_1");
        addReplica("monitoring_queue_2");
    }

    public void addReplica(String replicaQueueName) {
        int hash = calculateHash(replicaQueueName);
        circle.put(hash, replicaQueueName);
        System.out.println("LOG: Added Replica '" + replicaQueueName + "' at hash " + hash);
    }

    public String getTargetReplica(String deviceId) {
        if (circle.isEmpty()) {
            return null;
        }

        int hash = calculateHash(deviceId);

        if (!circle.containsKey(hash)) {
            SortedMap<Integer, String> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }

        return circle.get(hash);
    }

    private int calculateHash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return ((digest[0] & 0xFF) << 24) |
                    ((digest[1] & 0xFF) << 16) |
                    ((digest[2] & 0xFF) << 8)  |
                    ((digest[3] & 0xFF));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}