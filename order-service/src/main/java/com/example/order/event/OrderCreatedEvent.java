package com.example.order.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(Long orderId, Long userId, String userName, BigDecimal amount, Instant createdAt) {
}
