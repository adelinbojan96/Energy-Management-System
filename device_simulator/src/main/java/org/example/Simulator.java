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

public class Simulator {

    private static final String QUEUE_NAME = "data_queue";

    public static void main(String[] args) {
        System.out.println("--- Energy Data Simulator Started ---");

        try {
            Properties props = new Properties();
            try (InputStream input = Simulator.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    System.out.println("Sorry, unable to find config.properties");
                    return;
                }
                props.load(input);
            }

            String deviceId = props.getProperty("device.id");
            String host = props.getProperty("rabbitmq.host");
            
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");

            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                ObjectMapper mapper = new ObjectMapper();
                Random random = new Random();
                double currentConsumption = 5.0; 

                System.out.println("Sending data for Device ID: " + deviceId);

                while (true) {
                    long timestamp = System.currentTimeMillis();
                    double fluctuation = (random.nextDouble() - 0.5) * 1.5; 
                    currentConsumption += fluctuation;
                    if (currentConsumption < 0.1) currentConsumption = 0.1;

                    Measurement measurement = new Measurement(
                            timestamp,
                            UUID.fromString(deviceId),
                            currentConsumption
                    );

                    String json = mapper.writeValueAsString(measurement);

                    channel.basicPublish("", QUEUE_NAME, null, json.getBytes());
                    System.out.println(" [x] Sent: " + json);

                    TimeUnit.SECONDS.sleep(3); 
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    record Measurement(long timestamp, UUID device_id, double measurement_value) {}
}