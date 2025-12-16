package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String SIMULATOR_QUEUE = "device.data.input.queue"; 
    public static final String SIMULATOR_EXCHANGE = "sync-exchange";       
    public static final String SIMULATOR_KEY = "device.data";               

    public static final String REPLICA_1_QUEUE = "monitoring_queue_1";
    public static final String REPLICA_2_QUEUE = "monitoring_queue_2";

    @Bean
    public Queue simulatorQueue() {
        return new Queue(SIMULATOR_QUEUE, true);
    }
    @Bean
    public TopicExchange simulatorExchange() {
        return new TopicExchange(SIMULATOR_EXCHANGE);
    }
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(simulatorQueue()).to(simulatorExchange()).with(SIMULATOR_KEY);
    }

    @Bean
    public Queue replica1Queue() {
        return new Queue(REPLICA_1_QUEUE, true);
    }
    @Bean
    public Queue replica2Queue() {
        return new Queue(REPLICA_2_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}