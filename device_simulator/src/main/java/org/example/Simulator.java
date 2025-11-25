package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime; 

public class Simulator {


    public static void main(String[] args) {
        System.out.println("--- Energy Data Simulator Started ---");

        try {
            Properties props = new Properties();
            try (InputStream input = Simulator.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.err.println("Error: Unable to find config.properties");
                    return;
                }
                props.load(input);
            }

            String deviceId = props.getProperty("device.id");
            String host = props.getProperty("rabbitmq.host");
            String exchangeName = props.getProperty("rabbitmq.exchange.name");
            String routingKeyData = props.getProperty("rabbitmq.routing.key.data");

            if (deviceId == null || host == null || exchangeName == null || routingKeyData == null) {
                System.err.println("Error: Missing RabbitMQ/Device properties in config.properties");
                return;
            }

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                
                channel.exchangeDeclare(exchangeName, "topic", true); 

                ObjectMapper mapper = new ObjectMapper();
                Random random = new Random();
                double currentConsumption = 5.0; 
                
                final long INTERVAL_SECONDS = 600; 

                System.out.println("Sending data for Device ID: " + deviceId);
                System.out.println("Publishing to Exchange: " + exchangeName + ", Key: " + routingKeyData);
                
                while (true) {
                    long timestamp = System.currentTimeMillis();
                    
                    double fluctuation = (random.nextDouble() - 0.5) * 0.5; 
                    currentConsumption += fluctuation;
                    if (currentConsumption < 0.1) currentConsumption = 0.1;

                    Measurement measurement = new Measurement(
                            timestamp,
                            UUID.fromString(deviceId),

                            random.nextDouble() * 1.4 + 0.1 
                    );

                    String json = mapper.writeValueAsString(measurement);

                    channel.basicPublish(exchangeName, routingKeyData, null, json.getBytes());
                    
                    System.out.println(" [x] Sent at " + LocalDateTime.now() + ": " + json);
                    TimeUnit.SECONDS.sleep(INTERVAL_SECONDS); 
                }
            }

        } catch (Exception e) {
            System.err.println("Simulator Error: " + e.getMessage());
        }
    }

    record Measurement(long timestamp, UUID device_id, double measurement_value) {}
}