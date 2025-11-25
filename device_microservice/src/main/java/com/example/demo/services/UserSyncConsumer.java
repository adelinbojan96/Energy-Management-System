package com.example.demo.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.example.demo.dtos.UserSyncDTO;

@Service
public class UserSyncConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.user.sync}")
    public void consumeUserSync(UserSyncDTO message) {
        System.out.println("Device Service received User Sync: " + message.getUserId());
    }
}