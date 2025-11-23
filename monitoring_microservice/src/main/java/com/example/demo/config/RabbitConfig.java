package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.exchange.sync}")
    private String syncExchange;

    @Value("${rabbitmq.queue.data}")
    private String dataQueue;

    @Value("${rabbitmq.queue.sync.monitoring}")
    private String monitoringSyncQueue;

    @Bean
    public Queue dataQueue() {
        return new Queue(dataQueue, true, false, false);
    }

    @Bean
    public FanoutExchange syncExchange() {
        return new FanoutExchange(syncExchange);
    }

    @Bean
    public Queue monitoringSyncQueue() {
        return new Queue(monitoringSyncQueue, true, false, false);
    }

    @Bean
    public Binding binding(Queue monitoringSyncQueue, FanoutExchange syncExchange) {
        return BindingBuilder.bind(monitoringSyncQueue).to(syncExchange);
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