package com.example.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotNull Long userId,
        @NotNull @DecimalMin("0.01") BigDecimal amount) {
}
