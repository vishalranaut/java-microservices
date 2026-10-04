package com.example.notification;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final SimpMessagingTemplate messaging;

    public OrderEventListener(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    /**
     * Pure push, no database writes, so redelivery of a duplicate message is harmless (idempotent).
     * After the configured retries fail, the message is dead-lettered.
     */
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        messaging.convertAndSend("/topic/orders", event);
        messaging.convertAndSend("/topic/users/" + event.userId(), event);
    }
}
