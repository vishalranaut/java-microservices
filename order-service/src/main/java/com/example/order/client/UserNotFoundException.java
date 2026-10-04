package com.example.order.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Business outcome, not an infrastructure failure: ignored by the circuit breaker. */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "User does not exist")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("User not found with id " + id);
    }
}
