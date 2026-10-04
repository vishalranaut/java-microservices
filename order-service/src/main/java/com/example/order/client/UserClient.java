package com.example.order.client;

import com.example.contracts.user.GetUserReply;
import com.example.contracts.user.GetUserRequest;
import com.example.contracts.user.UserServiceGrpc;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/** gRPC client for user-service, wrapped in a deadline and a circuit breaker. */
@Component
public class UserClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    @CircuitBreaker(name = "userService", fallbackMethod = "unavailable")
    public GetUserReply getUser(long id) {
        try {
            return stub.withDeadlineAfter(2, TimeUnit.SECONDS)
                    .getUser(GetUserRequest.newBuilder().setId(id).build());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException(id);
            }
            throw e;
        }
    }

    // Fallbacks are chosen by exception type. UserNotFoundException matches neither,
    // so it propagates unchanged and returns 422 to the caller.
    private GetUserReply unavailable(long id, CallNotPermittedException e) {
        throw new UserServiceUnavailableException();
    }

    private GetUserReply unavailable(long id, StatusRuntimeException e) {
        throw new UserServiceUnavailableException();
    }
}
