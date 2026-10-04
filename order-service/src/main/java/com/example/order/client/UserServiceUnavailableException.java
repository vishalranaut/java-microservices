package com.example.order.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE, reason = "User service unavailable")
public class UserServiceUnavailableException extends RuntimeException {
    public UserServiceUnavailableException() {
        super("User service unavailable");
    }
}
