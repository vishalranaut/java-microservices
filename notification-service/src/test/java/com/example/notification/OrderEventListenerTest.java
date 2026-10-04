package com.example.notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class OrderEventListenerTest {

    @Test
    void pushesToGlobalAndPerUserTopics() {
        SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
        OrderEventListener listener = new OrderEventListener(messaging);
        OrderCreatedEvent event = new OrderCreatedEvent(10L, 7L, "Asha", new BigDecimal("49.99"), Instant.now());

        listener.onOrderCreated(event);

        verify(messaging).convertAndSend("/topic/orders", event);
        verify(messaging).convertAndSend("/topic/users/7", event);
    }
}
