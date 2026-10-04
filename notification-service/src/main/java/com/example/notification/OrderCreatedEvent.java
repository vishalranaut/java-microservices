package com.example.notification;

import java.math.BigDecimal;
import java.time.Instant;

/** Own copy of the event shape: services share the contract, not a class. */
public record OrderCreatedEvent(Long orderId, Long userId, String userName, BigDecimal amount, Instant createdAt) {
}
