package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.exchange.name}")
    private String syncExchange;

    @Value("${rabbitmq.queue.data}")
    private String dataQueue;

    @Value("${rabbitmq.queue.sync.monitoring}")
    private String monitoringSyncQueue;

    @Value("${rabbitmq.routing.key.data}")
    private String dataRoutingKey;

    @Bean
    public Queue dataQueue() {
        return new Queue(dataQueue, true, false, false);
    }

    @Bean
    public Queue monitoringSyncQueue() {
        return new Queue(monitoringSyncQueue, true, false, false);
    }

    @Bean
    public TopicExchange syncExchange() {
        return new TopicExchange(syncExchange);
    }

    @Bean
    public Binding syncEventsBinding(Queue monitoringSyncQueue, TopicExchange syncExchange) {
        return BindingBuilder.bind(monitoringSyncQueue).to(syncExchange).with("#");
    }

    @Bean
    public Binding dataBinding(Queue dataQueue, TopicExchange syncExchange) {
        return BindingBuilder.bind(dataQueue).to(syncExchange).with(dataRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}