package com.example.order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(Long id, Long userId, BigDecimal amount, String status, Instant createdAt) {

    static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getUserId(), o.getAmount(), o.getStatus().name(), o.getCreatedAt());
    }
}
